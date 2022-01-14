package com.lepine.transfers.data;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles({"test" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockDataTest {

    private final String
            VALID_ITEM = "Item1",
            VALID_WAREHOUSE = "Warehouse1";

    private final int VALID_STOCK_QUANTITY = 10;

    @Autowired
    private StockRepo stockRepo;

    @BeforeEach
    void setup() {
        stockRepo.deleteAll();
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("VKKagaZFlZ: Test stock creation")
    void testStockCreation() {

        // Arrange
        final Stock stock = Stock.builder()
                .Item(VALID_ITEM)
                .Warehouse(VALID_WAREHOUSE)
                .Quantity(VALID_STOCK_QUANTITY)
                .build();

        // Act
        final Stock create = stockRepo.save(stock);

        // Assert
        assertThat(create).isNotNull();
        assertThat(create.getUuid()).isNotNull();
        assertThat(create.getItem()).isEqualTo(VALID_ITEM);
        assertThat(create.getWarehouse()).isEqualTo(VALID_WAREHOUSE);
        assertThat(create.getQuantity()).isEqualTo(VALID_STOCK_QUANTITY);
    }

}
