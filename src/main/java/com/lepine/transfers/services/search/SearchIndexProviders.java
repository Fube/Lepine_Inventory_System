package com.lepine.transfers.services.search;

import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.lepine.transfers.data.item.ItemSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SearchIndexProviders {

    private final SearchClient searchClient;

    @Bean
    public SearchIndex<ItemSearchDTO> getItemSearchIndex() {
        return searchClient.initIndex("items", ItemSearchDTO.class);
    }
}
