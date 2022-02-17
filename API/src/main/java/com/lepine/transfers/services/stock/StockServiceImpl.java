package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.*;
import com.lepine.transfers.events.item.ItemDeleteEvent;
import com.lepine.transfers.events.item.ItemDeleteHandler;
import com.lepine.transfers.events.item.ItemUpdateEvent;
import com.lepine.transfers.events.item.ItemUpdateHandler;
import com.lepine.transfers.events.shipment.ShipmentCreateEvent;
import com.lepine.transfers.events.shipment.ShipmentCreateHandler;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.exceptions.stock.StockAlreadyExistsException;
import com.lepine.transfers.exceptions.stock.StockNotFoundException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.item.ItemService;
import com.lepine.transfers.services.search.SearchService;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class StockServiceImpl implements StockService, ItemUpdateHandler, ItemDeleteHandler, ShipmentCreateHandler {

    private final StockRepo stockRepo;
    private final StockMapper stockMapper;
    private final SearchService<StockSearchDTO, UUID> searchService;
    private final ItemService itemService;
    private final WarehouseService warehouseService;

    @Override
    public Stock create(StockUuidLessItemUuidWarehouseUuid dto) {
        log.info("Create stock {}", dto);

        log.info("Checking for existing (item, warehouse) pair");
        final Optional<Stock> foundStock = stockRepo
                .findByItemUuidAndWarehouseUuid(dto.getItemUuid(), dto.getWarehouseUuid());
        if(foundStock.isPresent()) {
            log.info("Found existing (item, warehouse) pair");
            throw new StockAlreadyExistsException(foundStock.get());
        }

        log.info("Searching for item {}", dto.getItemUuid());
        itemService.findByUuid(dto.getItemUuid()).orElseThrow(() -> new ItemNotFoundException(dto.getItemUuid()));
        log.info("Item exists");

        log.info("Searching for warehouse {}", dto.getWarehouseUuid());
        warehouseService.findByUuid(dto.getWarehouseUuid()).orElseThrow(() -> new WarehouseNotFoundException(dto.getWarehouseUuid()));
        log.info("Warehouse exists");

        final Stock mapped = stockMapper.toEntity(dto);
        log.info("Mapped stock {}", mapped);
        final Stock stock = stockRepo.save(mapped);
        log.info("Stock created {}", stock);

        log.info("Indexing stock {}", stock);
        final StockSearchDTO searchDTO = stockMapper.toSearchDTO(stock);
        log.info("Mapped to search DTO {}", searchDTO);
        searchService.index(searchDTO);
        log.info("Stock indexed {}", stock);

        return stock;
    }

    @Override
    public Optional<Stock> findByUuid(UUID uuid) {
        log.info("Searching for stock with UUID {}", uuid);
        return stockRepo.findById(uuid);
    }

    @Override
    public Page<Stock> findAll(PageRequest pageRequest) {
        log.info("Retrieving for all stocks");
        final Page<Stock> all = stockRepo.findAll(pageRequest);
        log.info("Retrieved {} stocks", all.getTotalElements());

        return all;
    }

    @Override
    public Stock update(UUID uuid, StockUuidLessItemLessWarehouseLess dto) {
        log.info("Updating stock with UUID {} with update request {}", uuid, dto);
        // NOTE: Might be able to use mapper here but eeeeeeeeeeeeeeeeeeeeeeeeeeeh it's easier like this for now
        final Stock stock = findByUuid(uuid).orElseThrow(() -> new StockNotFoundException(uuid));
        log.info("Found stock {}", stock);

        stock.setQuantity(dto.getQuantity());
        final Stock updated = stockRepo.save(stock);
        log.info("Updated stock {}", updated);

        log.info("Indexing stock {}", updated);
        final StockSearchDTO searchDTO = stockMapper.toSearchDTO(updated);
        log.info("Mapped to search DTO {}", searchDTO);
        searchService.index(searchDTO);
        log.info("Stock indexed {}", updated);

        return updated;
    }

    public void onItemUpdate(ItemUpdateEvent event) {
        log.info("Reacting to item update");
        updateSearchIndexFor(event.getItem());
    }

    @Override
    public void updateSearchIndexFor(Item item) {
        log.info("Updating search index for item with UUID {}", item.getUuid());

        final List<Stock> affected = stockRepo.findByItemUuid(item.getUuid());

        final List<StockSearchDTO> asSearchDTOs =
                affected.parallelStream()
                                .map(stockMapper::toSearchDTO)
                                .collect(Collectors.toList());
        log.info("Mapped to search DTOs");

        searchService.partialUpdateAllInBatch(asSearchDTOs);
    }

    @Override
    @Transactional
    public void delete(UUID dto) {
        log.info("Deleting stock with UUID {}", dto);
        final int affected = stockRepo.deleteByUuid(dto);
        log.info("Deleted {} stocks", affected);
    }

    @Override
    public Set<Stock> findByUuidIn(Set<UUID> uuids) {
        log.info("Searching for stocks with UUIDs {}", uuids);
        final Set<Stock> stocks = stockRepo.findDistinctByUuidIn(uuids);
        log.info("Found {} stocks", stocks.size());

        return stocks;
    }

    @Override
    public void onItemDelete(ItemDeleteEvent event) {
        log.info("Reacting to item delete");
        deleteSearchIndexFor(event.getUuid());
    }

    private void deleteSearchIndexFor(UUID uuid) {
        log.info("Deleting search index for item with UUID {}", uuid);

        final List<Stock> affected = stockRepo.findByItemUuid(uuid);

        final List<StockSearchDTO> asSearchDTOs =
                affected.parallelStream()
                                .map(stockMapper::toSearchDTO)
                                .collect(Collectors.toList());
        log.info("Mapped to search DTOs");

        searchService.deleteAllInBatch(asSearchDTOs);
    }

    @Override
    public void onShipmentCreate(ShipmentCreateEvent event) {
        log.info("Reacting to shipment create");
        final List<StockSearchDTO> affected = event.getShipment()
                .getTransfers()
                .parallelStream()
                .map(transfer -> stockMapper.toSearchDTO(transfer.getStock()))
                .collect(Collectors.toList());
        log.info("Found {} affected stocks", affected.size());

        searchService.partialUpdateAllInBatch(affected);
    }
}
