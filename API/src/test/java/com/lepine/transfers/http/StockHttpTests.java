package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.StockController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockUuidLessItemLessWarehouseLess;
import com.lepine.transfers.data.stock.StockUuidLessItemUuidWarehouseUuid;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.services.stock.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;
import java.util.function.Consumer;

import static java.lang.String.format;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { StockController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")
public class StockHttpTests {

    private final static String
            ITEM_NOT_FOUND_ERROR_FORMAT = "Item with uuid %s not found",
            WAREHOUSE_NOT_FOUND_ERROR_FORMAT = "Warehouse with uuid %s not found",
            STOCK_NOT_FOUND_ERROR_FORMAT = "Stock with uuid %s not found";

    private final static int VALID_QUANTITY = 10;

    private final static UUID
            VALID_ITEM_UUID = UUID.randomUUID(),
            NON_EXISTENT_ITEM_UUID = UUID.randomUUID(),
            VALID_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID(),
            NON_EXISTENT_STOCK_UUID = UUID.randomUUID();

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
    private MockMvc mockMvc;

    @Autowired
    private StockController stockController;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockService stockService;

    @Test
    void contextLoads() {}

    private ResultActions createWith(final StockUuidLessItemUuidWarehouseUuid given) throws Exception {
        return createWith(given, stubbing -> stubbing.willReturn(VALID_STOCK));
    }

    private ResultActions createWith(
            final StockUuidLessItemUuidWarehouseUuid given,
            final Consumer<BDDMockito.BDDMyOngoingStubbing<Stock>> arrangement) throws Exception {
        // Arrange
        final String asString = objectMapper.writeValueAsString(given);
        arrangement.accept(given(stockService.create(given)));

        // Act
        return mockMvc.perform(post("/stocks")
                .contentType(APPLICATION_JSON)
                .content(asString));
    }

    @Test
    @DisplayName("ttkHeVrxAm: Given POST on /stocks with valid stock as manager, then return created (201, stock)")
    @WithMockUser(username = "some-manager", roles = {"MANAGER"})
    void create_AsManager() throws Exception {

        // Act & Assert
        createWith(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID)
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(VALID_STOCK)));
    }

    @Test
    @DisplayName("KawvOaaxEK: Given POST on /stocks with valid stock as clerk, then return created (403, error)")
    @WithMockUser(username = "some-clerk", roles = {"CLERK"})
    void create_AsClerk() throws Exception {

        // Act & Assert
        createWith(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("UotbhAcAiZ: Given POST on /stocks with valid stock as salesperson, then return created (403, error)")
    @WithMockUser(username = "some-salesperson", roles = {"SALESPERSON"})
    void create_AsSalesperson() throws Exception {

        // Act & Assert
        createWith(VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HLndZYAwbH: Given POST on /stocks with non-existent item as manager, then return bad request (404, error)")
    @WithMockUser(username = "some-manager", roles = {"MANAGER"})
    void create_NonExistentItem() throws Exception {

        // Arrange
        final StockUuidLessItemUuidWarehouseUuid given = VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID.toBuilder()
                .itemUuid(NON_EXISTENT_ITEM_UUID)
                .build();

        // Act & Assert
        createWith(given, stubbing -> stubbing.willThrow(new ItemNotFoundException(NON_EXISTENT_ITEM_UUID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(format(ITEM_NOT_FOUND_ERROR_FORMAT, NON_EXISTENT_ITEM_UUID)))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
