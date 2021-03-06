package com.lepine.transfers.data.warehouse;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WarehouseRepo extends JpaRepository<Warehouse, UUID> {
    Integer deleteByUuid(UUID uuid);

    Optional<Warehouse> findByZipCode(String zipCode);

    Optional<Warehouse> findByUuid(UUID uuid);
}
