package com.lepine.transfers.services.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentMapper;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.exceptions.stock.StockNotFoundException;
import com.lepine.transfers.exceptions.stock.StockTooLowException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepo shipmentRepo;
    private final ShipmentMapper shipmentMapper;
    private final StockService stockService;
    private final WarehouseService warehouseService;

    @Override
    @Transactional
    public Shipment create(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO) {
        log.info("Creating shipment with order number {}", shipmentStatusLessUUIDLessDTO.getOrderNumber());

        final UUID to = shipmentStatusLessUUIDLessDTO.getTo();
        log.info("Checking for existence of target warehouse {}", to);
        warehouseService.findByUuid(to)
                .orElseThrow(() -> new WarehouseNotFoundException(to));

        verifyStockExistence(shipmentStatusLessUUIDLessDTO);

        final Shipment shipment = shipmentMapper.toEntity(shipmentStatusLessUUIDLessDTO);
        final Shipment saved = shipmentRepo.save(shipment);

        log.info("Shipment with order number {} created as {}", saved.getOrderNumber(), saved.getUuid());

        return shipmentRepo.findOneByUuidEagerLoad(saved.getUuid());
    }

    private void verifyStockExistence(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO) {
        log.info("Checking for existence of all stocks");

        final List<TransferUuidLessDTO> uuidLessDTOTransfers = shipmentStatusLessUUIDLessDTO.getTransfers();
        final List<UUID> uuidLessDTOTransfersMappedToStockUuid = uuidLessDTOTransfers.parallelStream()
                .map(TransferUuidLessDTO::getStockUuid).collect(Collectors.toList());
        final Set<Stock> byUuidIn = stockService.findByUuidIn(uuidLessDTOTransfersMappedToStockUuid);

        log.info("Found {} stocks", byUuidIn.size());

        // NOTE: This can probably be optimized
        for (TransferUuidLessDTO uuidLessDTOTransfer : uuidLessDTOTransfers) {
            final Optional<Stock> any = byUuidIn.parallelStream()
                    .filter(s -> s.getUuid().equals(uuidLessDTOTransfer.getStockUuid()))
                    .findAny();

            final Stock stock = any.orElseThrow(() -> new StockNotFoundException(uuidLessDTOTransfer.getStockUuid()));

            if(stock.getQuantity() < uuidLessDTOTransfer.getQuantity()) {
                log.info("Stock {} has {} quantity, but {} was requested",
                        stock.getUuid(), stock.getQuantity(), uuidLessDTOTransfer.getQuantity());
                throw new StockTooLowException(stock.getUuid(), uuidLessDTOTransfer.getQuantity(), stock.getQuantity());
            }
        }
    }

    @Override
    public Page<Shipment> findAll(PageRequest pageRequest) {
        log.info("Finding all shipments for page {}", pageRequest);
        final Page<Shipment> all = shipmentRepo.findAll(pageRequest);
        log.info("Found {} shipments for page {}", all.getTotalElements(), pageRequest);

        return all;
    }
}
