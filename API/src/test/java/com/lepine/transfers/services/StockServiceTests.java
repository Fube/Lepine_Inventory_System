package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.stock.StockUuidLessItemUuidWarehouseUuid;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.services.search.SearchService;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.stock.StockServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { StockServiceImpl.class })
@ActiveProfiles({"test"})
public class StockServiceTests {

    private final static int VALID_QUANTITY = 10;
    private final static UUID
            VALID_ITEM_UUID = UUID.randomUUID(),
            VALID_WAREHOUSE_UUID = UUID.randomUUID();
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

    @Autowired
    private StockService stockService;

    @MockBean
    private StockRepo stockRepo;

    @MockBean
    private SearchService<StockSearchDTO, UUID> searchService;

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("efPRYsFzIE: Given valid stock when create, then return stock")
    void givenValidStock_whenCreate_thenReturnStock() {
        // Arrange
        final StockUuidLessItemUuidWarehouseUuid stock = StockUuidLessItemUuidWarehouseUuid.builder()
                .itemUuid(VALID_ITEM_UUID)
                .warehouseUuid(VALID_WAREHOUSE_UUID)
                .quantity(VALID_QUANTITY)
                .build();
        final Stock expected = VALID_STOCK;

        final ArgumentMatcher<Stock> stockArgumentMatcher = s ->
                s.getItem().getUuid().equals(VALID_ITEM_UUID) &&
                s.getWarehouse().getUuid().equals(VALID_WAREHOUSE_UUID) &&
                s.getQuantity() == VALID_QUANTITY;
        given(stockRepo.save(argThat(stockArgumentMatcher))).willReturn(expected);

        // Act
        final Stock given = stockService.create(stock);

        // Assert
        assertThat(given).isEqualTo(expected);
        verify(stockRepo, times(1)).save(argThat(stockArgumentMatcher));
        verify(searchService, times(1)).index(expected);
    }
}
