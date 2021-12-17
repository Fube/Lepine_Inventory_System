package com.lepine.transfers.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.controllers.GlobalAdvice;
import com.lepine.transfers.exceptions.auth.InvalidLoginException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: Might be able to make this better such that it accepts injections from the outside
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException | InvalidLoginException e) {
            log.error("Exception caught: {}", e.getMessage());

            final GlobalAdvice.HTTPErrorMessage httpErrorMessage =
                    new GlobalAdvice.HTTPErrorMessage(HttpStatus.FORBIDDEN.value(), e.getMessage());

            response.setStatus(httpErrorMessage.getStatus());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(httpErrorMessage));
        }
    }
}
