package com.lepine.transfers.services;

import com.lepine.transfers.data.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ItemService {
    Page<Item> findAll();

    Page<Item> findAll(PageRequest pageRequest);
}
