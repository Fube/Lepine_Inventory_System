package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseUUIDLessDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseService {
    Warehouse create(@Valid WarehouseActiveLessUUIDLessDTO warehouse);

    void delete(UUID uuid);

    Warehouse update(UUID uuid, @Valid WarehouseUUIDLessDTO toUpdate);

    Page<Warehouse> findAll();

    Page<Warehouse> findAll(PageRequest pageRequest);

    Optional<Warehouse> findByUuid(UUID uuid);
}
