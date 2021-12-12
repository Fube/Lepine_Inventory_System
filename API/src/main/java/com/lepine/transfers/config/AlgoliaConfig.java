package com.lepine.transfers.config;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "algolia")
@Data
public class AlgoliaConfig {

    @NotNull
    private String applicationId;

    @NotNull
    private String apiKey;

    @Bean
    public SearchClient getDefaultSearchClient() {
        return DefaultSearchClient.create(applicationId, apiKey);
    }
}
