package com.lepine.transfers.config;

import com.lepine.transfers.data.user.UserRepo;
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

import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepo userRepo;

    private static final Map<HttpMethod, List<String>> whiteListByMethod = Map.of();

    public SecurityConfig(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry =
                http.csrf().disable()
                .authorizeRequests()
                    .antMatchers("/users").hasRole("MANAGER");

        for(Map.Entry<HttpMethod, List<String>> entry : whiteListByMethod.entrySet()) {
            expressionInterceptUrlRegistry.antMatchers(entry.getValue().toArray(new String[0]));
        }

        expressionInterceptUrlRegistry
                .anyRequest().authenticated();
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
