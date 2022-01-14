package com.lepine.transfers.services;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.stock.StockServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {StockServiceImpl.class})
@ActiveProfiles({"test"})
public class StockServiceTests {

    private final String
            VALID_ITEM = "Item1",
            VALID_WAREHOUSE = "Warehouse1";

    private final UUID
            VALID_ID = UUID.randomUUID();

    private final int VALID_STOCK_QUANTITY = 10;

    @Autowired
    private StockService stockService;

    @MockBean
    private StockRepo stockRepo;

    @Test
    public void contextLoads() {
    }

    @Test
    @DisplayName("aWyGkloNJh: Given a valid stock when create, then return stock")
    public void givenAValidStockWhenCreateThenReturnStock() {
        // Arrange
        final Stock createStock = Stock.builder()
                .Item(VALID_ITEM)
                .Warehouse(VALID_WAREHOUSE)
                .Quantity(VALID_STOCK_QUANTITY)
                .build();
        given(stockRepo.save(createStock)).willReturn(createStock.toBuilder().uuid(VALID_ID).build());

        // Act
        final Stock result = stockService.create(createStock);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(VALID_ID);
        assertThat(result.getItem()).isEqualTo(VALID_ITEM);
        assertThat(result.getWarehouse()).isEqualTo(VALID_WAREHOUSE);
        assertThat(result.getQuantity()).isEqualTo(VALID_STOCK_QUANTITY);

        verify(stockRepo, atMostOnce()).save(createStock);
    }



}
