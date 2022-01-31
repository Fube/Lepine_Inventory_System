package com.lepine.transfers.unit.data;

import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.role.RoleRepo;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ConfirmationDataTests {

    private final static String
            VALID_WAREHOUSE_ZIP_CODE = "A1B2C3",
            VALID_TARGET_WAREHOUSE_ZIP_CODE = "A2B3C4",
            VALID_WAREHOUSE_CITY = "Some City",
            VALID_WAREHOUSE_PROVINCE = "Some Province",
            VALID_ITEM_NAME = "Some Item",
            VALID_ITEM_SKU = "Some SKU",
            VALID_ITEM_DESCRIPTION = "Some Description",
            VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number";

    private final static ShipmentStatus VALID_SHIPMENT_STATUS = ShipmentStatus.PENDING;

    private final static ZonedDateTime VALID_SHIPMENT_EXPECTED_DATE = ZonedDateTime.now().plusDays(3);

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

    private final static User VALID_USER = User.builder()
            .email("a@b.c")
            .password("somepassword")
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
            VALID_TRANSFER_UUID,
            VALID_SHIPMENT_UUID;

    final Shipment shipment = VALID_SHIPMENT.toBuilder()
            .transfers(List.of(VALID_TRANSFER.toBuilder().build()))
            .build();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ConfirmationRepo confirmationRepo;

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

    @BeforeEach
    void setUp() {
        // Warehouse & item persist
        VALID_WAREHOUSE_UUID = warehouseRepo.save(VALID_WAREHOUSE.toBuilder().build()).getUuid();
        VALID_TARGET_WAREHOUSE_UUID = warehouseRepo.save(VALID_TARGET_WAREHOUSE.toBuilder().build()).getUuid();

        VALID_ITEM_UUID = itemRepo.save(VALID_ITEM.toBuilder().build()).getUuid();
        entityManager.flush();
        VALID_WAREHOUSE.setUuid(VALID_WAREHOUSE_UUID);
        VALID_TARGET_WAREHOUSE.setUuid(VALID_TARGET_WAREHOUSE_UUID);
        VALID_ITEM.setUuid(VALID_ITEM_UUID);
        // End of Warehouse & item persist

        // Stock persist
        VALID_STOCK_UUID = stockRepo.save(VALID_STOCK.toBuilder().build()).getUuid();
        entityManager.flush();
        VALID_STOCK.setUuid(VALID_STOCK_UUID);
        // End of Stock persist

        // User persist
        final Role manager = roleRepo.findByName("MANAGER").get();

        VALID_USER.setRole(manager);
        VALID_USER_UUID = userRepo.save(VALID_USER).getUuid();
        entityManager.flush();
        VALID_USER.setUuid(VALID_USER_UUID);
        // End of User persist

        // Shipment & transfer persist
        VALID_SHIPMENT.setCreatedBy(VALID_USER_UUID);
        VALID_SHIPMENT.setTo(VALID_TARGET_WAREHOUSE_UUID);

        VALID_SHIPMENT_UUID = shipmentRepo.save(VALID_SHIPMENT.toBuilder().build()).getUuid();
        entityManager.flush();

        VALID_SHIPMENT.setUuid(VALID_SHIPMENT_UUID);
        VALID_TRANSFER_UUID = VALID_SHIPMENT.getTransfers().get(0).getUuid();
        // End of Shipment persist
    }

    @AfterEach
    void cleanUp() {
        entityManager.clear();
        confirmationRepo.deleteAll();
        shipmentRepo.deleteAll();
        transferRepo.deleteAll();
        stockRepo.deleteAll();
        itemRepo.deleteAll();
        warehouseRepo.deleteAll();
    }

    @Test
    void contextLoads() {}

    @ParameterizedTest(name = "{displayName} - {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("ebQHEVReTB: Given a valid transfer when confirm, then add confirmation for transfer")
    void testConfirmTransfer(final int quantity) {

        // Arrange
        final Confirmation given = Confirmation.builder()
                .transferUuid(VALID_TRANSFER_UUID)
                .quantity(quantity)
                .build();

        // Act
        final Confirmation confirmation = confirmationRepo.save(given);

        // Assert
        assertThat(confirmation).isNotNull();
        assertThat(confirmation.getUuid()).isNotNull();
        assertThat(confirmation.getTransferUuid()).isEqualTo(VALID_TRANSFER_UUID);
        assertThat(confirmation.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("BEzvsXbzvO: Given fully confirmed transfer when findAllFullyConfirmed, then return all fully confirmed transfers")
    void testFindAllFullyConfirmed() {

        // Arrange
        final Confirmation confirmation = Confirmation.builder()
                .transferUuid(VALID_TRANSFER_UUID)
                .quantity(VALID_STOCK_QUANTITY) // Fully confirm
                .build();

        final Confirmation saved = confirmationRepo.save(confirmation);
        entityManager.flush();

        // Act
        final List<Confirmation> confirmations = transferRepo.findAllFullyConfirmed();

        // Assert
        assertThat(confirmations).isNotEmpty();
        assertThat(confirmations).containsExactlyInAnyOrder(saved);
    }
}
