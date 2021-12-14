package com.lepine.transfers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Map<HttpMethod, List<String>> whiteListByMethod =
            Map.of(HttpMethod.POST, List.of("/users"));

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.csrf().disable()
                .authorizeRequests();

        for(Map.Entry<HttpMethod, List<String>> entry : whiteListByMethod.entrySet()) {
            expressionInterceptUrlRegistry.antMatchers(entry.getValue().toArray(new String[0]));
        }

        expressionInterceptUrlRegistry
                .anyRequest().authenticated();
    }
}
