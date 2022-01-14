package com.lepine.transfers.services;

import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.stock.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {StockServiceImpl.class})
@ActiveProfiles({"test"})
public class StockServiceTests {

    @Autowired
    private StockService stockService;

    @MockBean
    private StockRepo stockRepo;

    @Test
    public void contextLoads() {
    }
}
