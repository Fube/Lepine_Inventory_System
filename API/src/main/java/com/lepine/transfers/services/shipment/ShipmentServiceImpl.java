package com.lepine.transfers.services.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentMapper;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepo shipmentRepo;
    private final ShipmentMapper shipmentMapper;

    @Override
    @Transactional
    public Shipment create(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO) {
        log.info("Creating shipment with order number {}", shipmentStatusLessUUIDLessDTO.getOrderNumber());

        final Shipment shipment = shipmentMapper.toEntity(shipmentStatusLessUUIDLessDTO);
        final Shipment saved = shipmentRepo.save(shipment);

        log.info("Shipment with order number {} created as {}", saved.getOrderNumber(), saved.getUuid());

        return shipmentRepo.findOneByUuidEagerLoad(saved.getUuid());
    }

    @Override
    public Page<Shipment> findAll(PageRequest pageRequest) {
        log.info("Finding all shipments for page {}", pageRequest);
        final Page<Shipment> all = shipmentRepo.findAll(pageRequest);
        log.info("Found {} shipments for page {}", all.getTotalElements(), pageRequest);

        return all;
    }
}
