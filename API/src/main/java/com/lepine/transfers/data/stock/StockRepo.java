package com.lepine.transfers.data.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface StockRepo extends JpaRepository<Stock, UUID> {
    int deleteByUuid(UUID dto);
    List<Stock> findByItemUuid(UUID itemUuid);
    Set<Stock> findDistinctByUuidIn(Set<UUID> uuids);
    Optional<Stock> findByWarehouseUuidAndItemUuid(UUID uuid, UUID uuid1);
    Optional<Stock> findByItemUuidAndWarehouseUuid(UUID itemUuid, UUID warehouseUuid);
}
