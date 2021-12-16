package com.lepine.transfers.utils.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.JWTConfig;
import com.lepine.transfers.data.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserJWTUtilImpl implements JWTUtil<User> {

    private final Key key;
    private final ObjectMapper objectMapper;
    private final JWTConfig jwtConfig;

    public UserJWTUtilImpl(JWTConfig jwtConfig, ObjectMapper objectMapper) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.objectMapper = objectMapper;
    }

    @Override
    public String encode(User payload) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("role", payload.getRole());

        return Jwts.builder()
                .setSubject(payload.getEmail())
                .addClaims(claimsMap)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(key)
                .compact();
    }

    @Override
    public User decode(String token) {

        final Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        final Claims body = claimsJws.getBody();
        final User mappedUser = objectMapper.convertValue(body, User.class);

        return mappedUser.toBuilder()
                .email(body.getSubject())
                .build();
    }
}
