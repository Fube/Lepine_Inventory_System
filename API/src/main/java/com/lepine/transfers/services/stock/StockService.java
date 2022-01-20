package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockUuidLessItemLessWarehouseLess;
import com.lepine.transfers.data.stock.StockUuidLessItemUuidWarehouseUuid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

public interface StockService {
    Stock create(@Valid StockUuidLessItemUuidWarehouseUuid dto);

    Optional<Stock> findByUuid(UUID uuid);

    Page<Stock> findAll(PageRequest pageRequest);

    Stock update(UUID uuid, StockUuidLessItemLessWarehouseLess dto);
    void updateSearchIndexFor(Item item);

    void delete(UUID dto);
}
