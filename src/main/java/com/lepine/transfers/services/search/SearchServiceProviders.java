package com.lepine.transfers.services.search;

import com.algolia.search.SearchIndex;
import com.lepine.transfers.data.item.ItemSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SearchServiceProviders {

    private final SearchIndex<ItemSearchDTO> itemSearchIndex;

    @Bean
    public SearchService<ItemSearchDTO> getItemSearchService() {
        return new SearchServiceImpl<>(itemSearchIndex);
    }
}
