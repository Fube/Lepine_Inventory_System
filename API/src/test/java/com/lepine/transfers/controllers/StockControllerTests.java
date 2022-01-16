package com.lepine.transfers.controllers;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockUuidLessItemLessWarehouseLess;
import com.lepine.transfers.data.stock.StockUuidLessItemUuidWarehouseUuid;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.services.stock.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.UUID;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { ValidationConfig.class, StockController.class })
@ActiveProfiles({"test"})
public class StockControllerTests {

    private final static String
            ITEM_NOT_FOUND_ERROR_FORMAT = "Item with uuid %s not found",
            WAREHOUSE_NOT_FOUND_ERROR_FORMAT = "Warehouse with uuid %s not found",
            STOCK_NOT_FOUND_ERROR_FORMAT = "Stock with uuid %s not found";

    private final static int VALID_QUANTITY = 10;
    private final static UUID
            VALID_ITEM_UUID = UUID.randomUUID(),
            VALID_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID();
    private final static String
            VALID_ITEM_NAME = "Item",
            VALID_ITEM_DESCRIPTION = "Description",
            VALID_ITEM_SKU = "SKU",
            VALID_WAREHOUSE_ZIPCODE = "A1B2C3",
            VALID_WAREHOUSE_CITY = "City",
            VALID_WAREHOUSE_PROVINCE = "Province";

    private final static Item VALID_ITEM = Item.builder()
            .uuid(VALID_ITEM_UUID)
            .name(VALID_ITEM_NAME)
            .description(VALID_ITEM_DESCRIPTION)
            .sku(VALID_ITEM_SKU)
            .build();

    private final static Warehouse VALID_WAREHOUSE = Warehouse.builder()
            .uuid(VALID_WAREHOUSE_UUID)
            .zipCode(VALID_WAREHOUSE_ZIPCODE)
            .city(VALID_WAREHOUSE_CITY)
            .province(VALID_WAREHOUSE_PROVINCE)
            .build();

    private final static Stock VALID_STOCK = Stock.builder()
            .item(VALID_ITEM)
            .warehouse(VALID_WAREHOUSE)
            .quantity(VALID_QUANTITY)
            .build();

    private final static StockUuidLessItemLessWarehouseLess VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS =
            StockUuidLessItemLessWarehouseLess.builder()
                    .quantity(VALID_QUANTITY)
                    .build();

    private final static StockUuidLessItemUuidWarehouseUuid VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID =
            StockUuidLessItemUuidWarehouseUuid.builder()
                    .itemUuid(VALID_ITEM_UUID)
                    .warehouseUuid(VALID_WAREHOUSE_UUID)
                    .quantity(VALID_QUANTITY)
                    .build();

    @Autowired
    private StockController stockController;

    @MockBean
    private StockService stockService;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("JcXCHcgsfs: Given valid dto when create, then return stock")
    void create_Valid() {
        // Arrange
        given(stockService.create(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID))
                .willReturn(VALID_STOCK);

        // Act
        Stock result = stockController.create(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID);

        // Assert
        assertThat(result).isEqualTo(VALID_STOCK);
    }

    @Test
    @DisplayName("SoDsbTtVZX: Given invalid dto when create, then throw ConstraintViolationException")
    void create_InvalidDTO() {
        // Arrange
        final ConstraintViolationException expected = new ConstraintViolationException("", null);
        given(stockService.create(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID))
                .willThrow(expected);

        // Act & Assert
        assertThatThrownBy(() -> stockController.create(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("WyaujCIcwR: Given page and size when getAll, then return page")
    void getAll_PageAndSize() {
        // Arrange
        final int
                page = 1,
                size = 10;
        final PageRequest expectedPageRequest = PageRequest.of(page - 1, size);
        final Page<Stock> expectedPage = createPageFor(Collections.singletonList(VALID_STOCK));

        given(stockService.findAll(expectedPageRequest))
                .willReturn(expectedPage);

        // Act
        Page<Stock> result = stockController.getAll(page, size);

        // Assert
        assertThat(result.getTotalPages()).isEqualTo((int)Math.ceil(expectedPage.getTotalPages() / (double) size));
        assertThat(result.getTotalElements()).isEqualTo(expectedPage.getTotalElements());
        assertThat(result.getContent()).isEqualTo(expectedPage.getContent());
        assertThat(result.getNumber()).isEqualTo(page);

        verify(stockService, times(1)).findAll(expectedPageRequest);
    }
}
