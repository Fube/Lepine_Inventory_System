package com.lepine.transfers.services.search;

import com.algolia.search.SearchIndex;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.data.stock.StockSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SearchServiceProviders {

    private final SearchIndex<ItemSearchDTO> itemSearchIndex;
    private final SearchIndex<StockSearchDTO> stockSearchIndex;

    @Bean
    public SearchService<ItemSearchDTO, UUID> getItemSearchService() {
        return new SearchServiceImpl<>(itemSearchIndex);
    }

    @Bean
    public SearchService<StockSearchDTO, UUID> getStockSearchService() {
        return new SearchServiceImpl<>(stockSearchIndex);
    }
}
