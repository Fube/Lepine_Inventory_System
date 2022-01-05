package com.lepine.transfers.config;

import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.filters.auth.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepo userRepo;
    private final JWTFilter jwtFilter;

    @Value("${default-manager.username:admin}")
    private String DEFAULT_MANAGER_USERNAME;
    @Value("${default-manager.password:admin}")
    private String DEFAULT_MANAGER_PASSWORD;

    private static final Map<HttpMethod, List<String>> whiteListByMethod = Map.of(
            POST, List.of("/auth/login"),
            HEAD, List.of("/auth/logout")
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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/users/**").hasRole("MANAGER")
                    .antMatchers(POST, "/items", "/warehouses").hasRole("MANAGER")
                    .antMatchers(PUT, "/items/*", "/warehouses/*").hasRole("MANAGER")
                    .antMatchers(DELETE, "/items/*", "/warehouses/*").hasRole("MANAGER");


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

        final InMemoryUserDetailsManagerConfigurer inMem = new InMemoryUserDetailsManagerConfigurer();
        inMem.withUser(DEFAULT_MANAGER_USERNAME)
                .password("{noop}" + DEFAULT_MANAGER_PASSWORD)
                .roles("MANAGER");

        auth.apply(inMem);
        auth.apply(dao);
    }

    @Override @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
