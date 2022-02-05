package com.lepine.transfers.integration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.role.RoleRepo;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.stock.StockSearchDTO;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.exceptions.stock.StockNotFoundException;
import com.lepine.transfers.exceptions.stock.StockTooLowException;
import com.lepine.transfers.services.search.SearchService;
import com.lepine.transfers.services.shipment.ShipmentService;
import com.lepine.transfers.utils.date.ZonedDateUtils;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles({"test"})
public class ShipmentServiceTests {

    private final static String
            VALID_WAREHOUSE_ZIP_CODE = "A1B2C3",
            VALID_WAREHOUSE_CITY = "Some City",
            VALID_TARGET_WAREHOUSE_ZIP_CODE = "A2B3C4",
            VALID_WAREHOUSE_PROVINCE = "Some Province",
            VALID_ITEM_NAME = "Some Item",
            VALID_ITEM_SKU = "Some SKU",
            VALID_ITEM_DESCRIPTION = "Some Description",
            VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number";

    private final static ShipmentStatus VALID_SHIPMENT_STATUS = ShipmentStatus.PENDING;

    private final static ZonedDateTime VALID_SHIPMENT_EXPECTED_DATE = ZonedDateUtils.businessDaysFromNow(4);

    private final static int VALID_STOCK_QUANTITY = 10;

    private final static Warehouse VALID_WAREHOUSE = Warehouse.builder()
            .zipCode(VALID_WAREHOUSE_ZIP_CODE)
            .city(VALID_WAREHOUSE_CITY)
            .province(VALID_WAREHOUSE_PROVINCE)
            .build();

    private final static Warehouse VALID_TARGET_WAREHOUSE = VALID_WAREHOUSE.toBuilder()
            .zipCode(VALID_TARGET_WAREHOUSE_ZIP_CODE)
            .build();

    private final static Item VALID_ITEM = Item.builder()
            .sku(VALID_ITEM_SKU)
            .name(VALID_ITEM_NAME)
            .description(VALID_ITEM_DESCRIPTION)
            .build();

    private final static Stock VALID_STOCK = Stock.builder()
            .item(VALID_ITEM)
            .warehouse(VALID_WAREHOUSE)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final static Transfer VALID_TRANSFER = Transfer.builder()
            .stock(VALID_STOCK)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final static Shipment VALID_SHIPMENT = Shipment.builder()
            .status(VALID_SHIPMENT_STATUS)
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of(VALID_TRANSFER))
            .build();

    private UUID
            VALID_WAREHOUSE_UUID,
            VALID_TARGET_WAREHOUSE_UUID,
            VALID_ITEM_UUID,
            VALID_STOCK_UUID,
            VALID_USER_UUID,
            VALID_SHIPMENT_UUID,
            VALID_TRANSFER_UUID;

    private final TransferUuidLessDTO VALID_TRANSFER_UUID_LESS_DTO = TransferUuidLessDTO.builder()
            .stockUuid(VALID_STOCK_UUID)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final ShipmentStatusLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO = ShipmentStatusLessUuidLessDTO.builder()
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
            .build();

    private final User VALID_USER = User.builder()
            .email("a@b.c")
            .password("somePassword")
            .build();

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentRepo shipmentRepo;

    @Autowired
    private TransferRepo transferRepo;

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ConfirmationRepo confirmationRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private SearchService<StockSearchDTO, UUID> stockSearchService;

    private Shipment acceptDefaultShipment() {
        var shipment = shipmentRepo.findById(VALID_SHIPMENT_UUID).get();
        shipment.setStatus(ShipmentStatus.ACCEPTED);

        final Shipment saved = shipmentRepo.save(shipment);
        return shipmentRepo.findOneByUuidEagerLoad(saved.getUuid());
    }

    @Test
    void contextLoads() {}

    @BeforeEach
    void setUp() {
        VALID_WAREHOUSE_UUID = warehouseRepo.save(VALID_WAREHOUSE).getUuid();
        VALID_TARGET_WAREHOUSE_UUID = warehouseRepo.save(VALID_TARGET_WAREHOUSE).getUuid();
        VALID_ITEM_UUID = itemRepo.save(VALID_ITEM).getUuid();
        VALID_WAREHOUSE.setUuid(VALID_WAREHOUSE_UUID);
        VALID_TARGET_WAREHOUSE.setUuid(VALID_TARGET_WAREHOUSE_UUID);
        VALID_ITEM.setUuid(VALID_ITEM_UUID);

        VALID_STOCK_UUID = stockRepo.save(VALID_STOCK).getUuid();
        VALID_STOCK.setUuid(VALID_STOCK_UUID);

        VALID_TRANSFER_UUID_LESS_DTO.setStockUuid(VALID_STOCK_UUID);

        final Role manager = roleRepo.findByName("MANAGER").get();

        VALID_USER.setRole(manager);
        VALID_USER_UUID = userRepo.save(VALID_USER).getUuid();
        VALID_USER.setUuid(VALID_USER_UUID);

        VALID_SHIPMENT.setCreatedBy(VALID_USER_UUID);
        VALID_SHIPMENT.setTo(VALID_TARGET_WAREHOUSE_UUID);

        VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.setCreatedBy(VALID_USER_UUID);
        VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.setTo(VALID_TARGET_WAREHOUSE_UUID);

        final Shipment savedShipment = shipmentRepo.save(VALID_SHIPMENT);
        VALID_SHIPMENT_UUID = savedShipment.getUuid();

        VALID_TRANSFER_UUID = savedShipment.getTransfers().get(0).getUuid();
    }

    @AfterEach
    void cleanUp() {
        confirmationRepo.deleteAll();
        stockRepo.deleteAll();
        transferRepo.deleteAll();
        shipmentRepo.deleteAll();
        userRepo.deleteAll();
        warehouseRepo.deleteAll();
        itemRepo.deleteAll();
    }

    @Test
    @DisplayName("TzKMznSAph: Given valid DTO when create, then return transfer")
    void valid_Create() {

        // Act
        Shipment shipment = shipmentService.create(VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO);

        // Assert
        assertThat(shipment.getUuid()).isNotNull();
        assertThat(shipment.getStatus()).isEqualTo(VALID_SHIPMENT_STATUS);
        assertThat(shipment.getExpectedDate()).isEqualTo(VALID_SHIPMENT_EXPECTED_DATE);
        assertThat(shipment.getOrderNumber()).isEqualTo(VALID_SHIPMENT_ORDER_NUMBER);
        assertThat(shipment.getTransfers().get(0).getStock().getItem().getUuid()).isNotNull();

        assertThat(shipment.getTransfers().get(0).getStock())
                .usingRecursiveComparison()
                .ignoringFields("$$_hibernate_interceptor")
                .isEqualTo(VALID_STOCK.toBuilder()
                    .uuid(VALID_STOCK_UUID)
                    .quantity(VALID_STOCK_QUANTITY - VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.getTransfers().get(0).getQuantity())
                    .item(VALID_ITEM.toBuilder()
                            .uuid(VALID_ITEM_UUID)
                            .build())
                    .warehouse(VALID_WAREHOUSE.toBuilder()
                            .uuid(VALID_WAREHOUSE_UUID)
                            .build())
                    .build()
                );

        assertThat(stockRepo.findById(VALID_STOCK_UUID).get().getQuantity()).isEqualTo(
                VALID_STOCK_QUANTITY - VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.getTransfers().get(0).getQuantity());

        verify(stockSearchService, times(1)).partialUpdateAllInBatch(any());
    }

    @Test
    @DisplayName("bGTFRzciVo: Given valid DTO when create, check result is Jackson serializable")
    void valid_Create_JacksonSerializable() throws JsonProcessingException {

        // Act
        Shipment shipment = shipmentService.create(VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO);

        // Assert
        objectMapper.writeValueAsString(shipment);
    }

    @Test
    @DisplayName("MUTGjZadGQ: Given shipment with non-existent Stock when create, then throw StockNotFoundException")
    void non_existent_Stock_Create() {

        // Arrange
        final UUID nonExistentStockUuid = UUID.randomUUID();
        final ShipmentStatusLessUuidLessDTO given = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder().transfers(
                List.of(VALID_TRANSFER_UUID_LESS_DTO.toBuilder().stockUuid(nonExistentStockUuid).build()))
                .build();

        // Act
        final StockNotFoundException stockNotFoundException = catchThrowableOfType(
                () -> shipmentService.create(given), StockNotFoundException.class);

        // Assert
        assertThat(stockNotFoundException)
                .hasMessage(new StockNotFoundException(nonExistentStockUuid).getMessage());
    }

    @Test
    @DisplayName("vqENeTZanV: Given shipment with transfer quantity exceeding stock quantity when create, then throw StockTooLowException")
    void transfer_quantity_exceeding_stock_quantity_Create() {

        // Arrange
        final int wantedQuantity = VALID_STOCK.getQuantity() + 1;
        final int givenQuantity = VALID_STOCK.getQuantity();
        final ShipmentStatusLessUuidLessDTO given = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder().transfers(
                List.of(VALID_TRANSFER_UUID_LESS_DTO.toBuilder().quantity(wantedQuantity).build()))
                .build();

        // Act
        final StockTooLowException stockTooLowException = catchThrowableOfType(
                () -> shipmentService.create(given), StockTooLowException.class);

        // Assert
        assertThat(stockTooLowException)
                .hasMessage(new StockTooLowException(VALID_STOCK_UUID, givenQuantity, wantedQuantity).getMessage());
    }

    @Test
    @DisplayName("OfiSfVWruu: Given PageRequest when get, then return Page of Shipments")
    void valid_findAll() {

        // Arrange
        final PageRequest of = PageRequest.of(0, 10);
        final Shipment expected = acceptDefaultShipment();

        // Act
        Page<Shipment> shipments = shipmentService.findAll(of);

        // Assert
        assertThat(shipments.getTotalElements()).isEqualTo(1);
        assertThat(shipments.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("SrmPceFkyg: Given PageRequest when get, check result is Jackson serializable")
    void valid_findAll_JacksonSerializable() throws JsonProcessingException {

        // Arrange
        final PageRequest of = PageRequest.of(0, 10);
        final Shipment saved = shipmentRepo.save(VALID_SHIPMENT.toBuilder().build());

        // Act
        Page<Shipment> shipments = shipmentService.findAll(of);

        // Assert
        objectMapper.writeValueAsString(shipments);
    }

    @Test
    @DisplayName("qKQnksVZXM: Given valid user uuid when get by user, then return Page of Shipments for that user")
    void valid_findAllByUserUuid() {

        // Arrange
        final PageRequest of = PageRequest.of(0, 10);
        final Shipment expected = acceptDefaultShipment();

        // Act
        Page<Shipment> shipments = shipmentService.findAllByUserUuid(VALID_USER_UUID, of);

        // Assert
        assertThat(shipments.getTotalElements()).isEqualTo(1);
        assertThat(shipments.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("nXnvkoJALc: Given ACCEPTED shipments when findAllAccepted, then return Page of Shipments")
    void valid_findAllAccepted() {

        // Arrange
        final PageRequest of = PageRequest.of(0, 10);
        final Shipment saved = acceptDefaultShipment();

        // Act
        Page<Shipment> shipments = shipmentService.findAllAccepted(of);

        // Assert
        assertThat(shipments.getTotalElements()).isEqualTo(1);
        assertThat(shipments.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(saved);
    }

    @Test
    @DisplayName("fAHCBpnsGa: Given fully confirmed shipments when findAllFullyConfirmed, then return Page of Shipments")
    void valid_findAllFullyConfirmed() {

        // Arrange
        final PageRequest of = PageRequest.of(0, 10);

        acceptDefaultShipment();
        final Shipment expected = shipmentRepo.findOneByUuidEagerLoad(VALID_SHIPMENT_UUID);

        confirmationRepo.save(
                Confirmation.builder()
                        .transferUuid(VALID_TRANSFER_UUID)
                        .quantity(VALID_STOCK_QUANTITY)
                        .build());

        // Act
        Page<Shipment> shipments = shipmentService.findAllFullyConfirmed(of);

        // Assert
        assertThat(shipments.getTotalElements()).isEqualTo(1);
        assertThat(shipments.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("fcnvVtYRYK: Given fully confirmed shipment and valid time range when findAllFullyConfirmed, then return Page of Shipments")
    void valid_findAllFullyConfirmed_with_time_range() {

        // Arrange
        final PageRequest of = PageRequest.of(0, 10);
        final Shipment saved = acceptDefaultShipment();
        final ZonedDateTime from = ZonedDateTime.now().minusYears(100);
        final ZonedDateTime to = ZonedDateTime.now().plusYears(100);

        confirmationRepo.save(
                Confirmation.builder()
                        .transferUuid(VALID_TRANSFER_UUID)
                        .quantity(VALID_STOCK_QUANTITY)
                        .build());

        // Act
        Page<Shipment> shipments = shipmentService.findAllFullyConfirmed(from, to, of);

        // Assert
        assertThat(shipments.getTotalElements()).isEqualTo(1);
        assertThat(shipments.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(saved);
    }

}
