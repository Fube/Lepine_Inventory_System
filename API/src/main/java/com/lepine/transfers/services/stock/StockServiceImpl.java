package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockMapper;
import com.lepine.transfers.data.stock.StockRepo;

public class StockServiceImpl  implements StockService {

    private final StockRepo stockRepo;
    private final StockMapper stockMapper;



    @Override
    public Stock create(Stock stock) {
        return null;
    }
}
