package com.lepine.transfers.services.stock;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockMapper;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.stock.StockUUIDLessDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class StockServiceImpl implements StockService {

    private final StockRepo stockRepo;
    private final StockMapper stockMapper;

    @Override
    public Stock create(StockUUIDLessDTO stockUUIDLessDTO){

        // TODO: check if stock exists
        return null;
    }


}
