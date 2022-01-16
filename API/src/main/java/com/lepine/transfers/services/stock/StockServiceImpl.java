package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.stock.*;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
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

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepo stockRepo;
    private final StockMapper stockMapper;
    private final SearchService<StockSearchDTO, UUID> searchService;
    private final ItemService itemService;
    private final WarehouseService warehouseService;

    @Override
    public Stock create(StockUuidLessItemUuidWarehouseUuid dto) {
        log.info("Create stock {}", dto);

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

    @Override
    @Transactional
    public void delete(UUID dto) {
        log.info("Deleting stock with UUID {}", dto);
        final int affected = stockRepo.deleteByUuid(dto);
        log.info("Deleted {} stocks", affected);
    }
}
