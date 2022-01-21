package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.services.shipment.ShipmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test"})
public class ShipmentServiceTests {

    private final static String
            VALID_WAREHOUSE_ZIP_CODE = "A1B2C3",
            VALID_WAREHOUSE_CITY = "Some City",
            VALID_WAREHOUSE_PROVINCE = "Some Province",
            VALID_ITEM_NAME = "Some Item",
            VALID_ITEM_SKU = "Some SKU",
            VALID_ITEM_DESCRIPTION = "Some Description",
            VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number";

    private final static ShipmentStatus VALID_SHIPMENT_STATUS = ShipmentStatus.PENDING;

    private final static Date VALID_SHIPMENT_EXPECTED_DATE = new Date();

    private final static int VALID_STOCK_QUANTITY = 10;

    private final static Warehouse VALID_WAREHOUSE = Warehouse.builder()
            .zipCode(VALID_WAREHOUSE_ZIP_CODE)
            .city(VALID_WAREHOUSE_CITY)
            .province(VALID_WAREHOUSE_PROVINCE)
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

    private final static Shipment VALID_SHIPMENT = Shipment.builder()
            .status(VALID_SHIPMENT_STATUS)
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .build();

    private final static Transfer VALID_TRANSFER = Transfer.builder()
            .stock(VALID_STOCK)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private UUID
            VALID_WAREHOUSE_UUID,
            VALID_ITEM_UUID,
            VALID_STOCK_UUID,
            VALID_SHIPMENT_UUID;

    private final TransferUuidLessDTO VALID_TRANSFER_UUID_LESS_DTO = TransferUuidLessDTO.builder()
            .stockUuid(VALID_STOCK_UUID)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final ShipmentStatusLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO = ShipmentStatusLessUuidLessDTO.builder()
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
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

    @Test
    void contextLoads() {}

    @BeforeEach
    void setUp() {
        VALID_WAREHOUSE_UUID = warehouseRepo.save(VALID_WAREHOUSE).getUuid();
        VALID_ITEM_UUID = itemRepo.save(VALID_ITEM).getUuid();
        VALID_WAREHOUSE.setUuid(VALID_WAREHOUSE_UUID);
        VALID_ITEM.setUuid(VALID_ITEM_UUID);

        VALID_STOCK_UUID = stockRepo.save(VALID_STOCK).getUuid();
        VALID_STOCK.setUuid(VALID_STOCK_UUID);

        VALID_TRANSFER_UUID_LESS_DTO.setStockUuid(VALID_STOCK_UUID);
    }

    @AfterEach
    void cleanUp() {
        warehouseRepo.deleteAllInBatch();
        itemRepo.deleteAllInBatch();
        stockRepo.deleteAllInBatch();
        transferRepo.deleteAllInBatch();
        shipmentRepo.deleteAllInBatch();
    }

    @Test
    @DisplayName("TzKMznSAph: Given valid DTO when create, then return transfer")
    void valid_Create() {

        // Act
        Shipment shipment = shipmentService.create(VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO);

        // Assert
        System.out.println(shipment.getTransfers().get(0).getStock().getItem().getDescription());
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
                    .item(VALID_ITEM.toBuilder()
                            .uuid(VALID_ITEM_UUID)
                            .build())
                    .warehouse(VALID_WAREHOUSE.toBuilder()
                            .uuid(VALID_WAREHOUSE_UUID)
                            .build())
                    .build()
                );
    }
}
