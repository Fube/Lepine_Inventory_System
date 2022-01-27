package com.lepine.transfers.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class PageUtils {

    @SuppressWarnings("unchecked")
    public static <T> Page<T> createPageFor(List<T> items, PageRequest pageRequest) {
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), items.size());
        return new PageImpl<>(
                items.subList(start, end),
                pageRequest,
                items.size());
    }

    public static <T> Page<T> createPageFor(List<T> items) {
        return createPageFor(items, PageRequest.of(0, 10));
    }

}
