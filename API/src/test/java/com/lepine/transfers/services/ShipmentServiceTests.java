package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.services.shipment.ShipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {
})
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

    private final static UUID
            VALID_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_ITEM_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID(),
            VALID_SHIPMENT_UUID = UUID.randomUUID();

    private final static TransferUuidLessDTO VALID_TRANSFER_UUID_LESS_DTO = TransferUuidLessDTO.builder()
            .stockUuid(VALID_STOCK_UUID)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final static ShipmentStatusLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO = ShipmentStatusLessUuidLessDTO.builder()
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
            .build();

    @Autowired
    private ShipmentService shipmentService;

    @MockBean
    private ShipmentRepo shipmentRepo;

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("TzKMznSAph: Given valid DTO when create, then return transfer")
    void valid_Create() {

        // Arrange
        given(shipmentRepo.save(VALID_SHIPMENT)).willReturn(VALID_SHIPMENT);

        // Act
        Shipment shipment = shipmentService.create(VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO);

        // Assert
        assertThat(shipment).isEqualTo(VALID_SHIPMENT);
    }
}
