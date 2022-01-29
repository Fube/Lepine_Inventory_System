package com.lepine.transfers.services.shipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.data.shipment.*;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.events.shipment.ShipmentCreateEvent;
import com.lepine.transfers.events.shipment.ShipmentUpdateEvent;
import com.lepine.transfers.exceptions.shipment.ShipmentNotFoundException;
import com.lepine.transfers.exceptions.shipment.ShipmentNotPendingException;
import com.lepine.transfers.exceptions.stock.StockNotFoundException;
import com.lepine.transfers.exceptions.stock.StockTooLowException;
import com.lepine.transfers.exceptions.transfer.SameWarehouseException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepo shipmentRepo;
    private final ShipmentMapper shipmentMapper;
    private final StockService stockService;
    private final WarehouseService warehouseService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    @Transactional
    public Shipment create(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO) {
        log.info("Creating shipment with order number {}", shipmentStatusLessUUIDLessDTO.getOrderNumber());

        verifyWarehouseExistence(shipmentStatusLessUUIDLessDTO);


        final List<TransferUuidLessDTO> uuidLessDTOTransfers = shipmentStatusLessUUIDLessDTO.getTransfers();
        final HashMap<UUID, TransferUuidLessDTO> dtoTransfersByStockUuid = new HashMap<>(uuidLessDTOTransfers.size());
        uuidLessDTOTransfers.forEach(transferUuidLessDTO ->
                dtoTransfersByStockUuid.put(transferUuidLessDTO.getStockUuid(), transferUuidLessDTO));


        final Set<UUID> dtoStockUuids = dtoTransfersByStockUuid.keySet();
        final Set<Stock> byUuidIn = stockService.findByUuidIn(dtoStockUuids);
        log.info("Found {} stocks", byUuidIn.size());

        final HashMap<UUID, Stock> stockByUuid = new HashMap<>(byUuidIn.size());
        byUuidIn.forEach(s -> stockByUuid.put(s.getUuid(), s));

        log.info("Checking for existence of all stocks and their quantities");
        verifyStockExistenceAndQuantityAndMutate(
                dtoTransfersByStockUuid, dtoStockUuids, stockByUuid, shipmentStatusLessUUIDLessDTO.getTo());
        log.info("All stocks and quantities are valid");

        log.info("Mapping Shipment DTO to entity");
        final Shipment shipment = shipmentMapper.toEntity(shipmentStatusLessUUIDLessDTO);
        log.info("Mapped to entity");

        final Shipment saved = shipmentRepo.save(shipment);
        log.info("Shipment with order number {} created as {}", saved.getOrderNumber(), saved.getUuid());

        final Shipment oneByUuidEagerLoad = shipmentRepo.findOneByUuidEagerLoad(saved.getUuid());

        log.info("Publishing shipment created event");
        applicationEventPublisher.publishEvent(new ShipmentCreateEvent(this, oneByUuidEagerLoad));

        return oneByUuidEagerLoad;
    }

    private void verifyStockExistenceAndQuantityAndMutate(
            HashMap<UUID, TransferUuidLessDTO> dtoTransfersByStockUuid,
            Set<UUID> dtoStockUuids,
            HashMap<UUID, Stock> stockByUuid,
            UUID to
    ) {
        for (UUID uuid : dtoStockUuids) {
            final Stock stock = stockByUuid.get(uuid);
            if(stock == null) {
                throw new StockNotFoundException(uuid);
            }

            final int given = stock.getQuantity();
            final int wanted = dtoTransfersByStockUuid.get(uuid).getQuantity();
            if(given < wanted) {
                log.info("Stock {} has {} items, but {} was requested", stock.getUuid(), given, wanted);
                throw new StockTooLowException(stock.getUuid(), given, wanted);
            }

            final int newQuantity = given - wanted;
            log.info("Updating Stock {} quantity from {} to {}", stock.getUuid(), given, newQuantity);
            stock.setQuantity(newQuantity);

            if(stock.getWarehouse().getUuid().equals(to)) {
                log.info("Impossible to ship from warehouse to itself for stock {}", stock.getUuid());
                throw new SameWarehouseException(stock, to);
            }
        }
    }

    private void verifyWarehouseExistence(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO) {
        final UUID to = shipmentStatusLessUUIDLessDTO.getTo();
        log.info("Checking for existence of target warehouse {}", to);
        warehouseService.findByUuid(to)
                .orElseThrow(() -> new WarehouseNotFoundException(to));
    }

    @Override
    public Page<Shipment> findAll(PageRequest pageRequest) {
        log.info("Finding all shipments for page {}", pageRequest);
        final Page<Shipment> all = shipmentRepo.findAll(pageRequest);
        log.info("Found {} shipments for page {}", all.getTotalElements(), pageRequest);

        return all;
    }

    @Override
    public Page<Shipment> findAllByUserUuid(UUID userUuid, PageRequest pageRequest) {
        log.info("Finding all shipments for user {} for page {}", userUuid, pageRequest);
        final Page<Shipment> all = shipmentRepo.findAllByCreatedBy(userUuid, pageRequest);
        log.info("Found {} shipments for user {} for page {}", all.getTotalElements(), userUuid, pageRequest);

        return all;
    }

    @Override
    public Shipment update(UUID uuid, JsonPatch jsonPatch) {
        log.info("Applying patch {} to shipment {}", jsonPatch, uuid);
        final Shipment shipment = shipmentRepo.findById(uuid)
                .orElseThrow(() -> new ShipmentNotFoundException(uuid));
        final Shipment shipmentClone = shipment.toBuilder().build();

        if(shipment.getStatus() != ShipmentStatus.PENDING) {
            log.info("Shipment {} is not pending, cannot be updated", uuid);
            throw new ShipmentNotPendingException(uuid);
        }

        final ShipmentPatchDTO shipmentPatchDTO = shipmentMapper.toPatchDTO(shipment);
        JsonStructure target = objectMapper.convertValue(shipmentPatchDTO, JsonStructure.class);
        JsonStructure patched = jsonPatch.apply(target);

        final ShipmentPatchDTO backDTO = objectMapper.convertValue(patched, ShipmentPatchDTO.class);

        Set<ConstraintViolation<ShipmentPatchDTO>> violations = validator.validate(backDTO);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        final Shipment updated = shipmentMapper.toEntity(backDTO, shipment);

        final Shipment saved = shipmentRepo.save(updated);

        log.info("Updated shipment");

        log.info("Publishing update event for shipment {}", uuid);
        applicationEventPublisher.publishEvent(new ShipmentUpdateEvent(this, shipmentClone, saved));

        return saved;

    }
}
