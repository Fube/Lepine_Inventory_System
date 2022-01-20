package com.lepine.transfers.services.search;

import java.util.List;

public interface SearchService<T, I> {
    void index(T toIndex);
    void delete(I identifier);
    void partialUpdateAllInBatch(List<T> toIndex);
    void deleteAllInBatch(List<T> toDelete);
}
