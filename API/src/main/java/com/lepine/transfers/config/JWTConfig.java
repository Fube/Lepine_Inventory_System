package com.lepine.transfers.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt")
@Data
public class JWTConfig {

    @NotBlank
    @Size(min = 256, max = 256)
    private String secret;

    @NotNull
    @Min(1)
    private long expiration;
}
