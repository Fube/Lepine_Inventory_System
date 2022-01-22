package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.shipment.ShipmentController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatusLessCreatedByLessUuidLessDTO;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static com.lepine.transfers.utils.PageUtils.createPageFor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { ShipmentController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")
public class ShipmentHttpTests {

    private final static UUID
            VALID_SHIPMENT_UUID = UUID.randomUUID(),
            VALID_TARGET_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID(),
            VALID_MANAGER_UUID = UUID.randomUUID(),
            VALID_SALESPERSON_UUID = UUID.randomUUID(),
            VALID_CLERK_UUID = UUID.randomUUID(),
            VALID_ROLE_UUID = UUID.randomUUID();

    private final static int VALID_STOCK_QUANTITY = 10;

    private final static String
            VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number",
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
            .build();

    private String
            ERROR_MESSAGE_PAGINATION_PAGE_MIN,
            ERROR_MESSAGE_PAGINATION_SIZE_MIN,
            ERROR_MESSAGE_SHIPMENT_EXPECTED_DATE_TOO_EARLY,
            ERROR_MESSAGE_SHIPMENT_ORDER_NUMBER_NULL,
            ERROR_MESSAGE_SHIPMENT_TO_NULL;

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
}
