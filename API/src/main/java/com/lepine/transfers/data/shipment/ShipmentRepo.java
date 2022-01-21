package com.lepine.transfers.data.shipment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, UUID> {

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s where s.uuid = :uuid")
    Shipment findOneByUuidEagerLoad(UUID uuid);

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s")
    Page<Shipment> findAll(PageRequest pageRequest);
}
