package com.lepine.transfers.services.search;

import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.settings.IndexSettings;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.data.stock.StockSearchDTO;
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
        index.setSettings(new IndexSettings().setSearchableAttributes(Arrays.asList("sku", "name", "description")));
        return index;
    }

    @Bean
    public SearchIndex<StockSearchDTO> getStockSearchIndex() {
        final SearchIndex<StockSearchDTO> index = searchClient.initIndex("stocks", StockSearchDTO.class);
        index.setSettings(new IndexSettings().setSearchableAttributes(
                Arrays.asList("sku", "name", "description", "zipCode", "quantity")));
        return index;
    }
}
