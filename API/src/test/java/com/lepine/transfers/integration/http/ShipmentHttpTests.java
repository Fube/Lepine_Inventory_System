package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.shipment.ShipmentController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatusLessCreatedByLessUuidLessDTO;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.exceptions.auth.DefaultLoginNotAllowedException;
import com.lepine.transfers.exceptions.stock.StockTooLowException;
import com.lepine.transfers.exceptions.transfer.SameWarehouseException;
import com.lepine.transfers.services.shipment.ShipmentService;
import com.lepine.transfers.utils.MessageSourceUtils;
import com.lepine.transfers.utils.date.LocalDateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static com.lepine.transfers.utils.PageUtils.createPageFor;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { ShipmentController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")
public class ShipmentHttpTests {

    private final static UUID
            VALID_SHIPMENT_UUID = UUID.randomUUID(),
            VALID_TRANSFER_UUID = UUID.randomUUID(),
            VALID_TARGET_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID(),
            VALID_DEFAULT_LOGIN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
            VALID_MANAGER_UUID = UUID.randomUUID(),
            VALID_SALESPERSON_UUID = UUID.randomUUID(),
            VALID_CLERK_UUID = UUID.randomUUID(),
            VALID_ROLE_UUID = UUID.randomUUID();

    private final static int VALID_STOCK_QUANTITY = 10;

    private final static String
            VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number",
            VALID_DEFAULT_LOGIN_EMAIL = "manager",
            VALID_MANAGER_EMAIL = "a@b.c",
            VALID_SALESPERSON_EMAIL = "b@c.d",
            VALID_CLERK_EMAIL = "c@d.e",
            VALID_USER_PASSWORD = "noneofyourbusiness",
            VALID_CLERK_ROLE_NAME = "CLERK",
            VALID_MANAGER_ROLE_NAME = "MANAGER",
            VALID_SALESPERSON_ROLE_NAME = "SALESPERSON";

    private final static LocalDate VALID_SHIPMENT_EXPECTED_DATE = LocalDateUtils.businessDaysFromNow(3);

    private final static Role
            VALID_SALESPERSON_ROLE = Role.builder()
                .uuid(VALID_SALESPERSON_UUID)
                .name(VALID_SALESPERSON_ROLE_NAME)
                .build(),
            VALID_CLERK_ROLE = Role.builder()
                .uuid(VALID_ROLE_UUID)
                .name(VALID_CLERK_ROLE_NAME)
                .build(),
            VALID_MANAGER_ROLE = Role.builder()
                    .uuid(VALID_ROLE_UUID)
                    .name(VALID_MANAGER_ROLE_NAME)
                    .build();

    private final static Item VALID_ITEM = Item.builder()
            .uuid(UUID.randomUUID())
            .name("Item Name")
            .description("Item Description")
            .sku("Item SKU")
            .build();

    private final static Warehouse VALID_WAREHOUSE = Warehouse.builder()
            .uuid(VALID_TARGET_WAREHOUSE_UUID)
            .zipCode("A1B2C3")
            .city("City")
            .province("Province")
            .build();

    private final static Stock VALID_STOCK = Stock.builder()
            .uuid(VALID_STOCK_UUID)
            .warehouse(VALID_WAREHOUSE)
            .item(VALID_ITEM)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final static TransferUuidLessDTO VALID_TRANSFER_UUID_LESS_DTO = TransferUuidLessDTO.builder()
            .stockUuid(VALID_STOCK_UUID)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private final static ShipmentStatusLessCreatedByLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO =
            ShipmentStatusLessCreatedByLessUuidLessDTO.builder()
                    .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
                    .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
                    .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
                    .to(VALID_TARGET_WAREHOUSE_UUID)
                    .build();

    private final static ShipmentStatusLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO =
            ShipmentStatusLessUuidLessDTO.builder()
                    .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
                    .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
                    .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
                    .createdBy(VALID_MANAGER_UUID)
                    .to(VALID_TARGET_WAREHOUSE_UUID)
                    .build();

    private final static Shipment VALID_SHIPMENT = Shipment.builder()
            .uuid(VALID_SHIPMENT_UUID)
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of())
            .to(VALID_TARGET_WAREHOUSE_UUID)
            .build();

    private final static Transfer VALID_TRANSFER = Transfer.builder()
            .uuid(VALID_TRANSFER_UUID)
            .stock(VALID_STOCK)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private String
            ERROR_MESSAGE_PAGINATION_PAGE_MIN,
            ERROR_MESSAGE_PAGINATION_SIZE_MIN,
            ERROR_MESSAGE_SHIPMENT_EXPECTED_DATE_TOO_EARLY,
            ERROR_MESSAGE_SHIPMENT_ORDER_NUMBER_NULL,
            ERROR_MESSAGE_SHIPMENT_TO_NULL,
            ERROR_MESSAGE_SHIPMENT_TRANSFERS_EMPTY,
            ERROR_MESSAGE_SHIPMENT_TRANSFERS_NULL,
            ERROR_MESSAGE_SHIPMENT_TRANSFERS_SELF_TRANSFER;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShipmentController shipmentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    private UserRepo userRepo;

    @MockBean
    private ShipmentService shipmentService;

    @BeforeEach
    void setUp() {
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ERROR_MESSAGE_PAGINATION_PAGE_MIN = w.getMessage("pagination.page.min");
        ERROR_MESSAGE_PAGINATION_SIZE_MIN = w.getMessage("pagination.size.min");
        ERROR_MESSAGE_SHIPMENT_EXPECTED_DATE_TOO_EARLY = w.getMessage("shipment.expected.date.too.early");
        ERROR_MESSAGE_SHIPMENT_ORDER_NUMBER_NULL = w.getMessage("shipment.order.number.not_null");
        ERROR_MESSAGE_SHIPMENT_TO_NULL = w.getMessage("shipment.to.not_null");
        ERROR_MESSAGE_SHIPMENT_TRANSFERS_EMPTY = w.getMessage("shipment.transfers.size.min");
        ERROR_MESSAGE_SHIPMENT_TRANSFERS_NULL = w.getMessage("shipment.transfers.not_null");
        ERROR_MESSAGE_SHIPMENT_TRANSFERS_SELF_TRANSFER = w.getMessage("shipment.transfers.self_transfer");
    }

    @PostConstruct
    void preSetUp() {
        given(userRepo.findByEmail(VALID_MANAGER_EMAIL)).willReturn(Optional.ofNullable(User.builder()
                .uuid(VALID_MANAGER_UUID)
                .email(VALID_MANAGER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build()));

        given(userRepo.findByEmail(VALID_SALESPERSON_EMAIL)).willReturn(Optional.ofNullable(User.builder()
                .uuid(VALID_SALESPERSON_UUID)
                .email(VALID_SALESPERSON_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_SALESPERSON_ROLE)
                .build()));

        given(userRepo.findByEmail(VALID_CLERK_EMAIL)).willReturn(Optional.ofNullable(User.builder()
                .uuid(VALID_CLERK_UUID)
                .email(VALID_CLERK_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_CLERK_ROLE)
                .build()));

        given(userRepo.findByEmail(VALID_DEFAULT_LOGIN_EMAIL)).willReturn(Optional.ofNullable(User.builder()
                .uuid(VALID_DEFAULT_LOGIN_UUID)
                .email(VALID_DEFAULT_LOGIN_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build()));
    }


    @Test
    void contextLoads() {}

    @Test
    @DisplayName("IaQglpsLWX: Given GET on /shipments as manager, then return page of shipments (200, shipments)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void findAll_AsManager() throws Exception {

        // Arrange
        final PageRequest expectedPageRequest = PageRequest.of(0, 10, Sort.by("expectedDate").descending());
        final Page<Shipment> shipments = createPageFor(List.of(VALID_SHIPMENT), expectedPageRequest);
        given(shipmentService.findAll(expectedPageRequest)).willReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(shipments)));

        verify(shipmentService, times(1)).findAll(expectedPageRequest);
    }

    @Test
    @DisplayName("bKJKGQYgWu: Given GET on /shipments as salesperson, then return page of shipments created by salesperson (200, shipments)")
    @WithUserDetails(value = VALID_SALESPERSON_EMAIL)
    void findAll_AsSalesperson() throws Exception {

        // Arrange
        final PageRequest expectedPageRequest = PageRequest.of(0, 10, Sort.by("expectedDate").descending());
        final Page<Shipment> shipments = createPageFor(List.of(VALID_SHIPMENT), expectedPageRequest);
        given(shipmentService.findAllByUserUuid(VALID_SALESPERSON_UUID, expectedPageRequest))
                .willReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(shipments)));

        verify(shipmentService, times(1))
                .findAllByUserUuid(VALID_SALESPERSON_UUID, expectedPageRequest);
    }

    @Test
    @DisplayName("BIyMmmlbaJ: Given GET on /shipments as clerk, then return page of shipments (200, shipments)")
    @WithUserDetails(value = VALID_CLERK_EMAIL)
    void findAll_AsClerk() throws Exception {

        // Arrange
        final PageRequest expectedPageRequest = PageRequest.of(0, 10, Sort.by("expectedDate").descending());
        final Page<Shipment> shipments = createPageFor(List.of(VALID_SHIPMENT), expectedPageRequest);
        given(shipmentService.findAll(expectedPageRequest)).willReturn(shipments);

        // Act & Assert
        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(shipments)));

        verify(shipmentService, times(1)).findAll(expectedPageRequest);
        verify(shipmentService, never()).findAllByUserUuid(any(), any());
    }

    @Test
    @DisplayName("IuFRRCKGwG: Given POST on /shipments as manager, then create shipment (201, shipment)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_AsManager() throws Exception {

        // Arrange
        final Shipment expectedShipment = VALID_SHIPMENT.toBuilder()
                .transfers(List.of(VALID_TRANSFER))
                .build();

        final String expectedAsString = objectMapper.writeValueAsString(expectedShipment);
        final String givenAsString = objectMapper
                .writeValueAsString(VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO);

        given(shipmentService.create(any())).willReturn(expectedShipment);

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedAsString));

        verify(shipmentService, times(1)).create(any());
    }

    @Test
    @DisplayName("mYDtFOFUcM: Given POST on /shipments as salesperson, then create shipment (201, shipment)")
    @WithUserDetails(value = VALID_SALESPERSON_EMAIL)
    void create_AsSalesperson() throws Exception {

        // Arrange
        final Shipment expectedShipment = VALID_SHIPMENT.toBuilder()
                .transfers(List.of(VALID_TRANSFER))
                .build();

        final String expectedAsString = objectMapper.writeValueAsString(expectedShipment);
        final String givenAsString = objectMapper
                .writeValueAsString(VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO);

        given(shipmentService.create(any())).willReturn(expectedShipment);

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedAsString));

        verify(shipmentService, times(1)).create(any());
    }

    @Test
    @DisplayName("btYJfcKwbk: Given POST on /shipments as clerk, then deny access (403, error)")
    @WithUserDetails(value = VALID_CLERK_EMAIL)
    void create_AsClerk_DenyAccess() throws Exception {

        // Arrange
        final Shipment expectedShipment = VALID_SHIPMENT.toBuilder()
                .transfers(List.of(VALID_TRANSFER))
                .build();

        final String givenAsString = objectMapper
                .writeValueAsString(VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO);

        given(shipmentService.create(any())).willReturn(expectedShipment);

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isForbidden());

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("eSVJMMmpye: Given POST on /shipments with no transfers in body as manager, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_NoTransfers_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .transfers(Collections.emptyList())
                                .build());

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.transfers").isArray())
                .andExpect(jsonPath("$.errors.transfers[*]", containsInAnyOrder(
                        ERROR_MESSAGE_SHIPMENT_TRANSFERS_EMPTY
                )));

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("YHfbDPDSnZ: Given POST on /shipments with null transfers in body as manager, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_NullTransfers_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .transfers(null)
                                .build());

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.transfers").isArray())
                .andExpect(jsonPath("$.errors.transfers[*]", containsInAnyOrder(
                        ERROR_MESSAGE_SHIPMENT_TRANSFERS_NULL
                )));

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("hZRjgBWfSK: Given POST on /shipments with null 'to' as manager, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_NullTo_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .to(null)
                                .build());

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.to").isArray())
                .andExpect(jsonPath("$.errors.to[*]", containsInAnyOrder(
                        ERROR_MESSAGE_SHIPMENT_TO_NULL
                )));

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("NtnpkJyUcQ: Given POST on /shipments with null orderNumber as manager, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_NullOrderNumber_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .orderNumber(null)
                                .build());

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.orderNumber").isArray())
                .andExpect(jsonPath("$.errors.orderNumber[*]", containsInAnyOrder(
                        ERROR_MESSAGE_SHIPMENT_ORDER_NUMBER_NULL
                )));

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("syKcvTnAnB: Given POST on /shipments with expectedDate < 3 business days as manager, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_ExpectedDateLessThan3BusinessDays_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .expectedDate(LocalDate.now().minusDays(2))
                                .build());

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.expectedDate").isArray())
                .andExpect(jsonPath("$.errors.expectedDate[*]", containsInAnyOrder(
                        ERROR_MESSAGE_SHIPMENT_EXPECTED_DATE_TOO_EARLY.replace("{days}", "3")
                )));

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("tgiccCFNZV: Given POST on /shipments with self-transfer as manager, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_SelfTransfer_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .build());

        final SameWarehouseException expectedSameWarehouseException =
                new SameWarehouseException(VALID_STOCK, VALID_TARGET_WAREHOUSE_UUID);
        given(shipmentService.create(any()))
                .willThrow(expectedSameWarehouseException);

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedSameWarehouseException.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(shipmentService, times(1)).create(any());
    }

    @Test
    @DisplayName("JSFIWUKIcE: Given POST on /shipments when stock too low, then deny create (401, error)")
    @WithUserDetails(value = VALID_MANAGER_EMAIL)
    void create_StockTooLow_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .build());

        final StockTooLowException expectedStockTooLowException =
                new StockTooLowException(VALID_STOCK_UUID, VALID_STOCK_QUANTITY, VALID_STOCK_QUANTITY + 1);
        given(shipmentService.create(any()))
                .willThrow(expectedStockTooLowException);

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedStockTooLowException.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(shipmentService, times(1)).create(any());
    }

    @Test
    @DisplayName("ABDKaOoniX: Given POST on /shipments with default login, then deny create (400, error)")
    @WithUserDetails(value = VALID_DEFAULT_LOGIN_EMAIL)
    void create_DefaultLogin_DenyCreate() throws Exception {

        // Arrange
        final String givenAsString = objectMapper
                .writeValueAsString(
                        VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                                .build());

        // Act & Assert
        mockMvc.perform(post("/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenAsString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(new DefaultLoginNotAllowedException().getMessage()))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(shipmentService, never()).create(any());
    }

}
