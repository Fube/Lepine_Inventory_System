package com.lepine.transfers.data.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;
import java.lang.annotation.Annotation;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepo extends JpaRepository<Item, UUID> {
    Integer deleteByUuid(UUID uuid);

    Optional<Item> findBySkuIgnoreCase(String sku);

    @Query("select new com.lepine.transfers.data.item.ItemQuantityTuple(item, sum(transfer.quantity)) from Transfer transfer " +
            "join Item item " +
                "on transfer.stock.item.uuid = item.uuid " +
            "join Shipment shipment " +
                "on transfer.shipmentUuid = shipment.uuid " +
            "where shipment.expectedDate between :start and :end " +
            "group by item.uuid " +
                "order by sum(transfer.quantity) desc"
    )
    Page<ItemQuantityTuple> mostTransferredItemsInRange(ZonedDateTime start, ZonedDateTime end, Pageable pageable);
}

