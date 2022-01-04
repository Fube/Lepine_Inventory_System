package com.lepine.transfers.data.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WarehouseRepo extends JpaRepository<Warehouse, UUID> {
    Integer deleteByUuid(UUID uuid);

    Optional<Warehouse> findByZipCode(String zipCode);
}
