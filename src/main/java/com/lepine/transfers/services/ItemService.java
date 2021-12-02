package com.lepine.transfers.services;

import com.lepine.transfers.data.Item;
import org.springframework.data.domain.Page;

public interface ItemService {
    Page<Item> findAll();
}
