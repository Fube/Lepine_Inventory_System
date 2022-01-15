package com.lepine.transfers.data;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class StockDataTests {

    private UUID
            ITEM_UUID,
            WAREHOUSE_UUID;

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
        ITEM_UUID = itemRepo.save(new Item(ITEM_UUID, "Sku", "Description", "Name")).getUuid();
        WAREHOUSE_UUID = warehouseRepo.save(
                new Warehouse(WAREHOUSE_UUID, "Zip", "City", "Province", true)).getUuid();
        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        stockRepo.deleteAll();
        itemRepo.deleteAll();
        warehouseRepo.deleteAll();
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

    @Test
    @DisplayName("QDlLrANHUy: Given valid stock data with duplicate (item, warehouse), then throw ConstraintViolationException")
    void save_Duplicate() {
        // Arrange
        final int quantity = 10;
        final Stock stock = Stock.builder()
            .item(itemRepo.getById(ITEM_UUID))
            .warehouse(warehouseRepo.getById(WAREHOUSE_UUID))
            .quantity(quantity)
            .build();
        stockRepo.save(stock);
        entityManager.flush();

        // Act & Assert
        final Exception exception = assertThrows(Exception.class, () -> {
            stockRepo.save(stock.toBuilder().uuid(null).build());
            entityManager.flush();
        });

        final Throwable rootCause = NestedExceptionUtils.getRootCause(exception);
        assertThat(rootCause.getMessage()).contains("Unique index");

        // Clean up
        entityManager.clear(); // Remove dupe from entity manager
    }
}
