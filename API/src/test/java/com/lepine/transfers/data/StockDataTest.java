package com.lepine.transfers.data;

import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles({"test" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockDataTest {

    private static final Integer STOCK_QUANTITY = 10;

    private static final String VALID_ITEM_NAME = "validItemName";
    private static final String VALID_ITEM_DESCRIPTION = "validItemDescription";
    private static final String VALID_ITEM_SKU = "validItemSku";
    private static final Item VALID_ITEM= Item.builder()
            .name(VALID_ITEM_NAME)
            .description(VALID_ITEM_DESCRIPTION)
            .sku(VALID_ITEM_SKU)
            .build();

    private static final String VALID_WAREHOUSE_ZIPCODE = "validWarehouseZipcode";
    private static final String VALID_WAREHOUSE_CITY = "validWarehouseCity";
    private static final String VALID_WAREHOUSE_PROVINCE = "validWarehouseProvince";
    private static final Warehouse VALID_WAREHOUSE= Warehouse.builder()
            .zipCode(VALID_WAREHOUSE_ZIPCODE)
            .city(VALID_WAREHOUSE_CITY)
            .province(VALID_WAREHOUSE_PROVINCE)
            .build();

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setup() {
        stockRepo.deleteAll();
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("VKKagaZFlZ: Given valid stock data, when stock is created, then stock is saved")
    void testStockCreation() {

        // Arrange
        final Stock stock = Stock.builder()
                .item(VALID_ITEM)
                .warehouse(VALID_WAREHOUSE)
                .Quantity(STOCK_QUANTITY)
                .build();

        // Act
        final Stock create = stockRepo.save(stock);

        // Assert
        assertThat(create).isNotNull();
        assertThat(create.getUuid()).isNotNull();
        assertThat(create.getItem()).isEqualTo(VALID_ITEM);
        assertThat(create.getWarehouse()).isEqualTo(VALID_WAREHOUSE);
        assertThat(create.getQuantity()).isEqualTo(STOCK_QUANTITY);
    }

    @Test
    @DisplayName("VkScIOuPoB: Given stock with null sku, when stock is created, then throw exception")
    void testStockCreationWithNullSku() {

        // Arrange
        final Stock stock = Stock.builder()
                .item(null)
                .warehouse(VALID_WAREHOUSE)
                .Quantity(STOCK_QUANTITY)
                .build();

        // Act
        final EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> stockRepo.save(stock));

        // Assert
        assertThat(exception).isNotNull();
        final Throwable cause = NestedExceptionUtils.getRootCause(exception);

        assertThat(cause).isNotNull();
        assertThat(cause.getMessage()).isEqualTo("Item cannot be null");
    }

    @Test
    @DisplayName("asupDXFwsX: Given stock with null warehouse, when stock is created, then throw exception")
    void testStockCreationWithNullWarehouse() {

        // Arrange
        final Stock stock = Stock.builder()
                .item(VALID_ITEM)
                .warehouse(null)
                .Quantity(STOCK_QUANTITY)
                .build();

        // Act
        final EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> stockRepo.save(stock));

        // Assert
        assertThat(exception).isNotNull();
        final Throwable cause = NestedExceptionUtils.getRootCause(exception);

        assertThat(cause).isNotNull();
        assertThat(cause.getMessage()).isEqualTo("Warehouse cannot be null");
    }

    @Test
    @DisplayName("YTESvqhPRp: Given stock with null quantity, when stock is created, then throw exception")
    void testStockCreationWithNullQuantity() {

        // Arrange
        final Stock stock = Stock.builder()
                .item(VALID_ITEM)
                .warehouse(VALID_WAREHOUSE)
                .Quantity(null)
                .build();

        // Act
        final EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> stockRepo.save(stock));

        // Assert
        assertThat(exception).isNotNull();
        final Throwable cause = NestedExceptionUtils.getRootCause(exception);

        assertThat(cause).isNotNull();
        assertThat(cause.getMessage()).isEqualTo("Quantity cannot be null");
    }

    @Test
    @DisplayName("bqGeGSkOjf: Given stock with negative quantity, when stock is created, then throw exception")
    void testStockCreationWithNegativeQuantity() {

        // Arrange
        final Stock stock = Stock.builder()
                .item(VALID_ITEM)
                .warehouse(VALID_WAREHOUSE)
                .Quantity(-1)
                .build();

        // Act
        final EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> stockRepo.save(stock));

        // Assert
        assertThat(exception).isNotNull();
        final Throwable cause = NestedExceptionUtils.getRootCause(exception);

        assertThat(cause).isNotNull();
        assertThat(cause.getMessage()).isEqualTo("Quantity cannot be negative");
    }

}
