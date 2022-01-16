package com.lepine.transfers.services.item;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.events.item.ItemUpdateEvent;
import com.lepine.transfers.exceptions.item.DuplicateSkuException;
import com.lepine.transfers.services.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;
    private final SearchService<ItemSearchDTO, UUID> searchService;
    private final ItemMapper itemMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Page<Item> findAll() {
        return itemRepo.findAll(PageRequest.of(0, 10));
    }

    @Override
    public Page<Item> findAll(PageRequest pageRequest) {
        log.info("retrieving all items");
        final Page<Item> all = itemRepo.findAll(pageRequest);
        log.info("retrieved all items");

        return all;
    }

    @Override
    public Item create(Item item) {
        log.info("creating item");

        log.info("checking for dupe SKU");
        if(itemRepo.findBySkuIgnoreCase(item.getSku()).isPresent()) {
            log.info("dupe SKU found");
            throw new DuplicateSkuException(item.getSku());
        }

        final Item created = itemRepo.save(item);
        log.info("created item");

        log.info("sending item to search service");
        searchService.index(itemMapper.toSearchDTO(created));
        log.info("sent item to search service");

        return created;
    }

    @Override
    public Item update(Item item) {
        log.info("updating item");

        log.info("checking for dupe SKU");
        final Optional<Item> bySkuIgnoreCase = itemRepo.findBySkuIgnoreCase(item.getSku());
        if(bySkuIgnoreCase.isPresent() && !bySkuIgnoreCase.get().getUuid().equals(item.getUuid())) {
            log.info("dupe SKU found");
            throw new DuplicateSkuException(item.getSku());
        }

        final Item updated = itemRepo.save(item);
        log.info("updated item");

        log.info("sending item to search service");
        searchService.index(itemMapper.toSearchDTO(updated));
        log.info("sent item to search service");

        log.info("Publish item update event");
        applicationEventPublisher.publishEvent(new ItemUpdateEvent(this, updated));

        return updated;
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        log.info("deleting item");
        final Integer deleted = itemRepo.deleteByUuid(uuid);
        if(deleted <= 0) {
            log.info("item not found");
            return;
        }
        log.info("deleted item");

        log.info("sending item to search service");
        searchService.delete(uuid);
        log.info("sent item to search service");
    }

    @Override
    public Optional<Item> findByUuid(UUID uuid) {
        log.info("retrieving item");
        final Optional<Item> item = itemRepo.findById(uuid);
        log.info("retrieved item");

        return item;
    }
}
