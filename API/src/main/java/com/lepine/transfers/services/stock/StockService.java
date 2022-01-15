package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockUUIDLessDTO;

import javax.validation.Valid;

public interface StockService {
    Stock create(@Valid StockUUIDLessDTO stockUUIDLessDTO);

}
