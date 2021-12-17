package com.lepine.transfers.config;

import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.filters.auth.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepo userRepo;
    private final JWTFilter jwtFilter;

    private static final Map<HttpMethod, List<String>> whiteListByMethod = Map.of(
            HttpMethod.POST, List.of("/auth/login")
    );

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry =
                http.csrf().disable()
                .formLogin().disable()
                .authorizeRequests()
                    .antMatchers("/users").hasRole("MANAGER");


        for(Map.Entry<HttpMethod, List<String>> entry : whiteListByMethod.entrySet()) {
            expressionInterceptUrlRegistry.antMatchers(entry.getKey(), entry.getValue().toArray(new String[0]))
                    .permitAll();
        }

        expressionInterceptUrlRegistry
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        final DaoAuthenticationConfigurer dao = new DaoAuthenticationConfigurer<>(username ->
                userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + "not found")));

        dao.passwordEncoder(passwordEncoder());

        auth.apply(dao);
    }

}
