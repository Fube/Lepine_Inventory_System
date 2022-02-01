package com.lepine.transfers.data.shipment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, UUID> {

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s where s.uuid = :uuid")
    Shipment findOneByUuidEagerLoad(UUID uuid);

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s")
    Page<Shipment> findAll(PageRequest pageRequest);

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s where s.createdBy = :createdBy")
    Page<Shipment> findAllByCreatedBy(@Param("createdBy") UUID createdBy, Pageable pageable);

    @Query("select s from Transfer t join Shipment s on t.shipmentUuid=s.uuid where t.uuid = :transferUuid")
    Optional<Shipment> findByTransferUuid(UUID transferUuid);
}
