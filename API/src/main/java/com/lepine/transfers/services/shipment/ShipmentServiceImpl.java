package com.lepine.transfers.services.shipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentMapper;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepo shipmentRepo;
    private final ShipmentMapper shipmentMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @SneakyThrows
    public Shipment create(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO) {
        log.info("Creating shipment with order number {}", shipmentStatusLessUUIDLessDTO.getOrderNumber());

        final Shipment shipment = shipmentMapper.toEntity(shipmentStatusLessUUIDLessDTO);
        final Shipment saved = shipmentRepo.save(shipment);

        log.info("Shipment with order number {} created as {}", saved.getOrderNumber(), saved.getUuid());

        return shipmentRepo.findOneByUuidEagerLoad(saved.getUuid()).orElseThrow(() -> new RuntimeException("Shipment not found"));
    }
}
