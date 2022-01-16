package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.stock.*;
import com.lepine.transfers.services.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepo stockRepo;
    private final SearchService<StockSearchDTO, UUID> searchService;

    @Override
    public Stock create(StockUuidLessItemUuidWarehouseUuid dto) {
        return null;
    }

    @Override
    public Optional<Stock> findByUuid(UUID dto) {
        return Optional.empty();
    }

    @Override
    public Page<Stock> findAll(PageRequest pageRequest) {
        return null;
    }

    @Override
    public Stock update(UUID uuid, StockUuidLessItemLessWarehouseLess dto) {
        return null;
    }

    @Override
    public void delete(UUID dto) {

    }
}
