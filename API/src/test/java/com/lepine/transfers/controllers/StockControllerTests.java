package com.lepine.transfers.controllers;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.services.stock.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { ValidationConfig.class, StockController.class })
@ActiveProfiles({"test"})
public class StockControllerTests {

    @MockBean
    private StockService stockService;

    @Test
    void contextLoads(){}


}
