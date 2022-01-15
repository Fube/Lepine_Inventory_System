package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.stock.StockUUIDLessDTO;
import com.lepine.transfers.data.warehouse.Warehouse;
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

    private final static UUID
            VALID_UUID = UUID.randomUUID();

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
        final StockUUIDLessDTO createStock = StockUUIDLessDTO.builder()
                .item(VALID_ITEM)
                .warehouse(VALID_WAREHOUSE)
                .quantity(STOCK_QUANTITY)
                .build();

        // given(stockRepo.save(createStock)).willReturn(createStock.toBuilder().uuid(VALID_UUID).build());

        // Act
        final Stock result = stockService.create(createStock);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(VALID_UUID);
        assertThat(result.getItem()).isEqualTo(VALID_ITEM);
        assertThat(result.getWarehouse()).isEqualTo(VALID_WAREHOUSE);
        assertThat(result.getQuantity()).isEqualTo(STOCK_QUANTITY);

        verify(stockRepo, atMostOnce()).save(createStock);
    }



}
