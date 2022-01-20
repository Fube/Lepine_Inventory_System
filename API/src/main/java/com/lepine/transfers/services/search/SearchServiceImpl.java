package com.lepine.transfers.services.search;

import com.algolia.search.SearchIndex;
import com.algolia.search.models.RequestOptions;
import com.algolia.search.models.indexing.ActionEnum;
import com.algolia.search.models.indexing.BatchOperation;
import com.algolia.search.models.indexing.BatchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl<T, I> implements SearchService<T, I> {

    private final SearchIndex<T> searchIndex;

    @Override
    public void index(T toIndex) {
        log.info("Indexing {}", toIndex);
        searchIndex.saveObject(toIndex);
        log.info("Indexed {}", toIndex);
    }

    @Override
    public void delete(I identifier) {
        log.info("Deleting {}", identifier);
        searchIndex.deleteObject(identifier.toString());
        log.info("Deleted {}", identifier);
    }

    @Override
    public void partialUpdateAllInBatch(List<T> toIndex) {
        log.info("Updating all {} items in batch", toIndex.size());
        searchIndex.batch(
                new BatchRequest<>(
                    toIndex.parallelStream()
                            .map(n -> new BatchOperation<>(ActionEnum.PARTIAL_UPDATE_OBJECT_NO_CREATE, n))
                            .collect(Collectors.toList()))
        );
        log.info("Updated {} items in batch", toIndex.size());
    }

    @Override
    public void deleteAllInBatch(List<T> toDelete) {
        log.info("Deleting all {} items in batch", toDelete.size());
        searchIndex.batch(
                new BatchRequest<>(
                        toDelete.parallelStream()
                                .map(n -> new BatchOperation<>(ActionEnum.DELETE_OBJECT, n))
                                .collect(Collectors.toList()))
        );
        log.info("Deleted {} items in batch", toDelete.size());
    }
}
