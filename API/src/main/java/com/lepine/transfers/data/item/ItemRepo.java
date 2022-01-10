package com.lepine.transfers.data.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepo extends JpaRepository<Item, UUID> {
    Integer deleteByUuid(UUID uuid);

    Optional<Item> findBySkuIgnoreCase(String sku);
}
