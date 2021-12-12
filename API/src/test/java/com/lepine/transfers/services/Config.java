package com.lepine.transfers.services;

import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.lepine.transfers.data.item.ItemSearchDTO;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class Config {
    @MockBean
    public SearchClient searchClient;
    @MockBean
    public SearchIndex<ItemSearchDTO> index;
}