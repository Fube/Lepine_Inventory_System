package com.lepine.transfers.services.item;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepo itemRepo;

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
}
