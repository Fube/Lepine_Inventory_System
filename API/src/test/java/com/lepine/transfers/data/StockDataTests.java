package com.lepine.transfers.data;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockDataTests {

    private final static UUID
        ITEM_UUID = UUID.randomUUID(),
        WAREHOUSE_UUID = UUID.randomUUID();

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        itemRepo.save(new Item(ITEM_UUID, "Sku", "Description", "Name"));
        warehouseRepo.save(new Warehouse(WAREHOUSE_UUID, "Zip", "City", "Province", true));
        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        itemRepo.deleteAll();
        warehouseRepo.deleteAll();
        stockRepo.deleteAll();
        entityManager.flush();
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("UXPSLjQjrx: Given valid stock data when save, then save stock")
    void save_Valid() {

        // Arrange
        final int quantity = 10;
        final Stock stock = Stock.builder()
            .item(itemRepo.getById(ITEM_UUID))
            .warehouse(warehouseRepo.getById(WAREHOUSE_UUID))
            .quantity(quantity)
            .build();

        // Act
        final Stock saved = stockRepo.save(stock);
        entityManager.flush();

        // Assert
        assertThat(stockRepo.count()).isEqualTo(1);
        assertThat(saved.getUuid()).isNotNull();
        assertThat(saved.getItem().getUuid()).isEqualTo(ITEM_UUID);
        assertThat(saved.getWarehouse().getUuid()).isEqualTo(WAREHOUSE_UUID);
        assertThat(saved.getQuantity()).isEqualTo(quantity);
    }
}
