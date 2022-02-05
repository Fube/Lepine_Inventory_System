package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.*;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.exceptions.stock.StockNotFoundException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.item.ItemService;
import com.lepine.transfers.services.search.SearchService;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.stock.StockServiceImpl;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
        MapperConfig.class,
        ValidationConfig.class,
        StockServiceImpl.class,
})
@ActiveProfiles({"test"})
public class StockServiceTests {

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

    private String
            ITEM_UUID_NULL_ERROR_MESSAGE,
            WAREHOUSE_UUID_NULL_ERROR_MESSAGE;

    @Autowired
    private StockService stockService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private StockRepo stockRepo;

    @MockBean
    private SearchService<StockSearchDTO, UUID> searchService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ITEM_UUID_NULL_ERROR_MESSAGE = w.getMessage("item.uuid.not_null");
        WAREHOUSE_UUID_NULL_ERROR_MESSAGE = w.getMessage("warehouse.uuid.not_null");
    }

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("efPRYsFzIE: Given valid stock when create, then return stock")
    void valid_Create() {
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

        given(stockRepo.save(argThat(stockArgumentMatcher)))
                .willReturn(expected);

        given(itemService.findByUuid(VALID_ITEM_UUID))
                .willReturn(Optional.ofNullable(VALID_ITEM));

        given(warehouseService.findByUuid(VALID_WAREHOUSE_UUID))
                .willReturn(Optional.ofNullable(VALID_WAREHOUSE));

        // Act
        final Stock given = stockService.create(stock);

        // Assert
        assertThat(given).isEqualTo(expected);
        verify(stockRepo, times(1)).save(argThat(stockArgumentMatcher));
        verify(searchService, times(1)).index(argThat(
                s ->    s.getObjectID().equals(VALID_STOCK.getUuid()) &&
                        s.getItemUuid().equals(VALID_ITEM_UUID) &&
                        s.getWarehouseUuid().equals(VALID_WAREHOUSE_UUID)
        ));
    }

    @Test
    @DisplayName("AayDHWicyB: Given non-existent item when create, then throw ItemNotFoundException")
    void create_NonExistentItem() {
        // Arrange
        final StockUuidLessItemUuidWarehouseUuid stock = StockUuidLessItemUuidWarehouseUuid.builder()
                .itemUuid(VALID_ITEM_UUID)
                .warehouseUuid(VALID_WAREHOUSE_UUID)
                .quantity(VALID_QUANTITY)
                .build();

        given(itemService.findByUuid(VALID_ITEM_UUID))
                .willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> stockService.create(stock))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage(format(ITEM_NOT_FOUND_ERROR_FORMAT, VALID_ITEM_UUID));
    }

    @Test
    @DisplayName("NdYraJcddd: Given non-existent warehouse when create, then throw WarehouseNotFoundException")
    void create_NonExistentWarehouse() {
        // Arrange
        final StockUuidLessItemUuidWarehouseUuid stock = StockUuidLessItemUuidWarehouseUuid.builder()
                .itemUuid(VALID_ITEM_UUID)
                .warehouseUuid(VALID_WAREHOUSE_UUID)
                .quantity(VALID_QUANTITY)
                .build();

        given(itemService.findByUuid(VALID_ITEM_UUID))
                .willReturn(Optional.ofNullable(VALID_ITEM));

        given(warehouseService.findByUuid(VALID_WAREHOUSE_UUID))
                .willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> stockService.create(stock))
                .isInstanceOf(WarehouseNotFoundException.class)
                .hasMessage(format(WAREHOUSE_NOT_FOUND_ERROR_FORMAT, VALID_WAREHOUSE_UUID));
    }

    @Test
    @DisplayName("HUjjRzdmnD: Given create with null itemUuid, then throw ConstraintViolationException")
    void create_NullItemUuid() {

        // Arrange
        final StockUuidLessItemUuidWarehouseUuid stock = VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID.toBuilder()
                .itemUuid(null)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> stockService.create(stock));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ITEM_UUID_NULL_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("NNUJzFFmRz: Given create with null warehouseUuid, then throw ConstraintViolationException")
    void create_NullWarehouseUuid() {

        // Arrange
        final StockUuidLessItemUuidWarehouseUuid stock = VALID_STOCK_UUID_LESS_ITEM_UUID_WAREHOUSE_UUID.toBuilder()
                .warehouseUuid(null)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> stockService.create(stock));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(WAREHOUSE_UUID_NULL_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("lXPsKuHBNw: Given non-existent stock when update, then throw StockNotFoundException")
    void update_NonExistentStock() {
        // Arrange
        final StockUuidLessItemUuidWarehouseUuid stock = StockUuidLessItemUuidWarehouseUuid.builder()
                .itemUuid(VALID_ITEM_UUID)
                .warehouseUuid(VALID_WAREHOUSE_UUID)
                .quantity(VALID_QUANTITY)
                .build();

        given(stockRepo.getById(VALID_STOCK_UUID))
                .willReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> stockService.update(VALID_STOCK_UUID, VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS))
                .isInstanceOf(StockNotFoundException.class)
                .hasMessage(format(STOCK_NOT_FOUND_ERROR_FORMAT, VALID_STOCK_UUID));
    }

    @Test
    @DisplayName("ZssguxpnKc: Given pagerequest when findAll, then return page of Stocks")
    void findAll_PageRequest() {
        // Arrange
        final PageRequest pageRequest = PageRequest.of(0, 10);
        final Page<Stock> page = Page.empty();
        given(stockRepo.findAll(pageRequest))
                .willReturn(page);

        // Act
        final Page<Stock> result = stockService.findAll(pageRequest);

        // Assert
        assertThat(result).isEqualTo(page);
    }

    @Test
    @DisplayName("JTNEbMKTSI: Given Stock not found when update, then throw StockNotFoundException")
    void update_StockNotFound() {
        // Arrange
        given(stockRepo.getById(VALID_STOCK_UUID))
                .willReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> stockService.update(VALID_STOCK_UUID, VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS))
                .isInstanceOf(StockNotFoundException.class)
                .hasMessage(format(STOCK_NOT_FOUND_ERROR_FORMAT, VALID_STOCK_UUID));
    }

    @Test
    @DisplayName("NNuilXzxIY: Given Stock exists when update, then update Stock")
    void update_StockExists() {
        // Arrange
        given(stockRepo.findById(VALID_STOCK_UUID))
                .willReturn(Optional.of(VALID_STOCK));

        // Act
        stockService.update(VALID_STOCK_UUID, VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS);

        // Assert
        verify(stockRepo).save(VALID_STOCK);
    }
}
