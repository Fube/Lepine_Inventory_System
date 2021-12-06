package com.lepine.transfers.services.item;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.services.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final SearchService<ItemSearchDTO> searchService;
    private final ItemMapper itemMapper;

    @Override
    public Page<Item> findAll() {
        return itemRepo.findAll(PageRequest.of(0, 10));
    }

    @Override
    public Page<Item> findAll(PageRequest pageRequest) {
        log.info("ItemController::getAll retrieving all items");
        final Page<Item> all = itemRepo.findAll(pageRequest);
        log.info("ItemController::getAll retrieved all items");

        return all;
    }

    @Override
    public Item create(Item item) {
        log.info("ItemController::create creating item");
        final Item created = itemRepo.save(item);
        log.info("ItemController::create created item");

        log.info("ItemController::create sending item to search service");
        searchService.index(itemMapper.toSearchDTO(created));
        log.info("ItemController::create sent item to search service");

        return created;
    }

    @Override
    public Item update(Item item) {
        log.info("ItemController::update updating item");
        final Item updated = itemRepo.save(item);
        log.info("ItemController::update updated item");

        log.info("ItemController::update sending item to search service");
        searchService.index(itemMapper.toSearchDTO(updated));
        log.info("ItemController::update sent item to search service");

        return updated;
    }

    @Override
    public void delete(UUID uuid) {

    }
}
