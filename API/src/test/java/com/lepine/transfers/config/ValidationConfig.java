package com.lepine.transfers.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@TestConfiguration
public class ValidationConfig extends MessageSourceConfig {

    @Bean
    public MethodValidationPostProcessor getMethodValidationPostProcessor() {
        final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(getValidator());
        return methodValidationPostProcessor;
    }
}
