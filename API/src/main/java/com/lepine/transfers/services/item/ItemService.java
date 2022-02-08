package com.lepine.transfers.services.item;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemQuantityTuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ItemService {

    Page<Item> findAll();

    Page<Item> findAll(PageRequest pageRequest);

    Item create(Item item);

    Item update(Item item);

    void delete(UUID uuid);

    Optional<Item> findByUuid(UUID uuid);

    Page<ItemQuantityTuple> findBestSellerForRange(ZonedDateTime from, ZonedDateTime to, PageRequest pageRequest);
}
