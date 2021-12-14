package com.lepine.transfers.services;

import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.lepine.transfers.services.search.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SearchServiceTests<T, I> {

    @Autowired
    protected SearchIndex<T> searchIndex;

    @Autowired
    protected SearchService<T, I> searchService;

    @Autowired
    protected SearchClient searchClient;

    @Test
    public abstract void testIndex();

    @Test
    public abstract void testDelete();
}
