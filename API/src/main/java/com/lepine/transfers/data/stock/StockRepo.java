package com.lepine.transfers.data.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockRepo extends JpaRepository<Stock, UUID> {
    int deleteByUuid(UUID dto);

    List<Stock> findByItemUuid(UUID itemUuid);
}
