package com.lepine.transfers.services.search;

public interface SearchService<T, I> {
    void index(T toIndex);

    void delete(I identifier);
}
