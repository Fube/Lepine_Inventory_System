package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.*;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.item.ItemService;
import com.lepine.transfers.services.search.SearchService;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    public Optional<Stock> findByUuid(UUID dto) {
        return Optional.empty();
    }

    @Override
    public Page<Stock> findAll(PageRequest pageRequest) {
        return null;
    }

    @Override
    public Stock update(UUID uuid, StockUuidLessItemLessWarehouseLess dto) {
        return null;
    }

    @Override
    public void delete(UUID dto) {

    }
}
