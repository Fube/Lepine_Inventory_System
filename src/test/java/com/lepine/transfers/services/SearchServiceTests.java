package com.lepine.transfers.services;

import com.algolia.search.SearchIndex;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SearchServiceTests<T> {

    @MockBean
    private SearchIndex<T> searchIndex;

    @Test
    public abstract void testIndex();
}
