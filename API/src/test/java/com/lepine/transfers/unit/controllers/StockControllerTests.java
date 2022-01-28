package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.StockController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockUuidLessItemLessWarehouseLess;
import com.lepine.transfers.data.stock.StockUuidLessItemUuidWarehouseUuid;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.exceptions.stock.StockNotFoundException;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


import static com.lepine.transfers.utils.PageUtils.createPageFor;
import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { ValidationConfig.class, StockController.class })
@ActiveProfiles({"test"})
public class StockControllerTests {

    private final static int VALID_QUANTITY = 10;

    private final static UUID
            VALID_ITEM_UUID = UUID.randomUUID(),
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

    private String
            ERROR_MESSAGE_PAGINATION_PAGE_MIN,
            ERROR_MESSAGE_PAGINATION_SIZE_MIN;

    @BeforeEach
    void setUp() {

        given(stockService.findByUuid(VALID_STOCK_UUID)).willReturn(Optional.ofNullable(VALID_STOCK));
        given(stockService.findByUuid(NON_EXISTENT_STOCK_UUID)).willReturn(Optional.empty());

        given(stockService.update(NON_EXISTENT_STOCK_UUID, VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS))
                .willThrow(new StockNotFoundException(NON_EXISTENT_STOCK_UUID));

        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ERROR_MESSAGE_PAGINATION_PAGE_MIN = w.getMessage("pagination.page.min");
        ERROR_MESSAGE_PAGINATION_SIZE_MIN = w.getMessage("pagination.size.min");
    }

    @AfterEach
    void tearDown() {
        reset(stockService);
    }

    @Autowired
    private StockController stockController;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

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

    @Test
    @DisplayName("SyhWlyNouY: Given page less than 1 when getAll, then throw ConstraintViolationException")
    void getAll_PageLessThan1_ThrowConstraintViolationException() {

        // Arrange
        final int pageNumber = 0, pageSize = 10;

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> stockController.getAll(pageNumber, pageSize));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        Assertions.assertThat(collect).containsExactly(ERROR_MESSAGE_PAGINATION_PAGE_MIN);

        verify(stockService, never()).findAll(any());
    }

    @Test
    @DisplayName("VKYZkdLAbA: Given page size less than 1 when getAll, then throw ConstraintViolationException")
    void getAll_PageSizeLessThan1_ThrowConstraintViolationException() {

        // Arrange
        final int pageNumber = 1, pageSize = 0;

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> stockController.getAll(pageNumber, pageSize));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        Assertions.assertThat(collect).containsExactly(ERROR_MESSAGE_PAGINATION_SIZE_MIN);

        verify(stockService, never()).findAll(any());
    }

    @Test
    @DisplayName("RlvpomZxKL: Given page and size less than 1 when getAll, then throw ConstraintViolationException")
    void getAll_PageAndSizeLessThan1_ThrowConstraintViolationException() {

        // Arrange
        final int pageNumber = 0, pageSize = 0;

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> stockController.getAll(pageNumber, pageSize));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        Assertions.assertThat(collect)
                .containsExactlyInAnyOrder(ERROR_MESSAGE_PAGINATION_PAGE_MIN, ERROR_MESSAGE_PAGINATION_SIZE_MIN);
    }

    @Test
    @DisplayName("TBJBPpXCrr: Given valid stock when update, then return stock")
    void update_ValidStock() {
        // Arrange
        final Stock expected = VALID_STOCK;
        final StockUuidLessItemLessWarehouseLess givenDTO = VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS;
        final UUID givenUuid = VALID_STOCK_UUID;
        given(stockService.update(givenUuid, givenDTO))
                .willReturn(expected);

        // Act
        final Stock result = stockController.update(givenUuid, givenDTO);

        // Assert
        assertThat(result).isEqualTo(expected);

        verify(stockService, times(1)).update(givenUuid, givenDTO);
    }

    @Test
    @DisplayName("lywxFvXnDc: Given valid dto for non-existing stock when update, then throw StockNotFoundException")
    void update_NonExistentStock() {

        // Arrange
        final StockUuidLessItemLessWarehouseLess givenDTO = VALID_STOCK_UUID_LESS_ITEM_LESS_WAREHOUSE_LESS;
        final UUID givenUuid = NON_EXISTENT_STOCK_UUID;

        // Act
        final StockNotFoundException stockNotFoundException =
                assertThrows(StockNotFoundException.class, () -> stockController.update(givenUuid, givenDTO));

        // Assert
        assertThat(stockNotFoundException.getMessage()).isEqualTo(
                new StockNotFoundException(givenUuid).getMessage());

        verify(stockService, times(1)).update(givenUuid, givenDTO);
    }
}
