package com.lepine.transfers.config;

import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.data.stock.StockSearchDTO;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class AlgoliaConfig {
    @MockBean
    public SearchClient searchClient;

    @MockBean
    public SearchIndex<ItemSearchDTO> itemIndex;

    @MockBean
    public SearchIndex<StockSearchDTO> stockIndex;
}
