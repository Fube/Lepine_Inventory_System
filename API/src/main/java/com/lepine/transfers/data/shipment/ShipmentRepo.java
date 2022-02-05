package com.lepine.transfers.data.shipment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, UUID> {

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s where s.uuid = :uuid")
    Shipment findOneByUuidEagerLoad(UUID uuid);

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s")
    Page<Shipment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    @Query("select s from Shipment s where s.createdBy = :createdBy")
    Page<Shipment> findAllByCreatedBy(@Param("createdBy") UUID createdBy, Pageable pageable);

    @Query("select s from Transfer t join Shipment s on t.shipmentUuid=s.uuid where t.uuid = :transferUuid")
    Optional<Shipment> findByTransferUuid(UUID transferUuid);

    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    Page<Shipment> findAllByStatus(ShipmentStatus status, Pageable pageable);

    @Query("select s from Shipment s " +
            "join Transfer t " +
                "on s.uuid = t.shipmentUuid " +
            "join Confirmation c " +
                "on t.uuid = c.transferUuid " +
            "group by s.uuid " +
                "having sum(c.quantity) = t.quantity"
    )
    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    Page<Shipment> findAllFullyConfirmed(Pageable pageable);

    @Query("select s from Shipment s " +
            "join Transfer t " +
                "on s.uuid = t.shipmentUuid " +
                    "and s.expectedDate between :start and :end " +
            "join Confirmation c " +
                "on t.uuid = c.transferUuid " +
            "group by s.uuid " +
                "having sum(c.quantity) = t.quantity"
    )
    @EntityGraph(attributePaths = {"transfers.stock", "transfers.stock.item", "transfers.stock.warehouse"})
    Page<Shipment> findAllFullyConfirmedInTimeRange(ZonedDateTime start, ZonedDateTime end, Pageable pageable);
}
