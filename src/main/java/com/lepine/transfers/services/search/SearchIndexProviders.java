package com.lepine.transfers.services.search;

import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.settings.IndexSettings;
import com.lepine.transfers.data.item.ItemSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SearchIndexProviders {

    private final SearchClient searchClient;

    @Bean
    public SearchIndex<ItemSearchDTO> getItemSearchIndex() {
        final SearchIndex<ItemSearchDTO> index = searchClient.initIndex("items", ItemSearchDTO.class);
        index.setSettings(new IndexSettings().setSearchableAttributes(Arrays.asList("SKU", "name")));
        return index;
    }
}