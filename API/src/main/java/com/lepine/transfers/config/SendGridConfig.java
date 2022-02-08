package com.lepine.transfers.config;

import com.sendgrid.SendGrid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "sendgrid")
@Data
public class SendGridConfig {

    @NotNull
    private String apiKey;

    @NotNull
    private String from;

    @Bean
    public SendGrid getSendGrid() {
        return new SendGrid(apiKey);
    }
}
