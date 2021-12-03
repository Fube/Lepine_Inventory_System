package com.lepine.transfers.services.search;

import com.algolia.search.SearchIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl<T> implements SearchService<T> {

    private final SearchIndex<T> searchIndex;

    @Override
    public void index(T toIndex) {
        log.info("Indexing {}", toIndex);
        searchIndex.saveObject(toIndex);
        log.info("Indexed {}", toIndex);
    }
}
