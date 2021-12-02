package com.lepine.transfers.services.item;

import com.lepine.transfers.data.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ItemService {
    Page<Item> findAll();

    Page<Item> findAll(PageRequest pageRequest);
}
