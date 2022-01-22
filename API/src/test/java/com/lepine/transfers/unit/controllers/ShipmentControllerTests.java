package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.shipment.ShipmentController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatusLessCreatedByLessUuidLessDTO;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.services.shipment.ShipmentService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import com.lepine.transfers.utils.date.LocalDateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { MapperConfig.class, ValidationConfig.class, ShipmentController.class })
@ActiveProfiles({"test"})
public class ShipmentControllerTests {

    private final static UUID
            VALID_SHIPMENT_UUID = UUID.randomUUID(),
            VALID_TARGET_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID(),
            VALID_USER_UUID = UUID.randomUUID(),
            VALID_ROLE_UUID = UUID.randomUUID();

    private final static int VALID_STOCK_QUANTITY = 10;

    private final static String
            VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number",
            VALID_USER_EMAIL = "a@b.c",
            VALID_USER_PASSWORD = "noneofyourbusiness",
            VALID_CLERK_ROLE_NAME = "CLERK",
            VALID_MANAGER_ROLE_NAME = "MANAGER";

    private final static LocalDate VALID_SHIPMENT_EXPECTED_DATE = LocalDateUtils.businessDaysFromNow(3);

    private final static Role
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
                    .createdBy(VALID_USER_UUID)
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
    private ShipmentController shipmentController;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;


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

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("UIHcfxYzbs: Given page and size when get, then return page of Shipments (findAllByUserUuid)")
    void valid_findAllByUserUuid() {

        // Arrange
        final int
                page = 1, // One-indexed
                size = 10;
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_CLERK_ROLE)
                .build();
        final PageRequest expectedPageRequest = PageRequest.of(page - 1, size, Sort.by("expectedDate").descending());
        final Page<Shipment> givenShipments = Page.empty();

        given(shipmentService.findAllByUserUuid(givenUser.getUuid(), expectedPageRequest))
                .willReturn(givenShipments);

        // Act
        final Page<Shipment> response = shipmentController.findAll(givenUser, page, size);

        // Assert
        assertThat(response).isEqualTo(givenShipments);

        verify(shipmentService, times(1))
                .findAllByUserUuid(
                        argThat(n -> n.equals(givenUser.getUuid())),
                        argThat(pr -> pr.getPageNumber() == page - 1
                                && pr.getPageSize() == size
                                && pr.getSort().equals(Sort.by("expectedDate").descending())));
        verify(shipmentService, never()).findAll(any());
    }

    @Test
    @DisplayName("sDTUKrHzBh: Given page and size when get, then return page of Shipments (findAll)")
    void valid_findAll() {

        // Arrange
        final int
                page = 1, // One-indexed
                size = 10;
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();
        final PageRequest expectedPageRequest = PageRequest.of(page - 1, size, Sort.by("expectedDate").descending());
        final Page<Shipment> givenShipments = Page.empty();

        given(shipmentService.findAll(expectedPageRequest))
                .willReturn(givenShipments);

        // Act
        final Page<Shipment> response = shipmentController.findAll(givenUser, page, size);

        // Assert
        assertThat(response).isEqualTo(givenShipments);

        verify(shipmentService, times(1))
                .findAll(
                        argThat(pr -> pr.getPageNumber() == page - 1
                                && pr.getPageSize() == size
                                && pr.getSort().equals(Sort.by("expectedDate").descending())));
        verify(shipmentService, never()).findAllByUserUuid(any(), any());
    }

    @Test
    @DisplayName("AeXLlOffFQ: Given page < 1 when get, then throw ConstraintViolationException")
    void invalid_PageLessThan1() {

        // Arrange
        final int
                page = 0, // One-indexed
                size = 10;
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException = catchThrowableOfType(
                () -> shipmentController.findAll(givenUser, page, size), ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PAGINATION_PAGE_MIN);

        verify(shipmentService, never()).findAllByUserUuid(any(), any());
        verify(shipmentService, never()).findAll(any());
    }

    @Test
    @DisplayName("HOnApKfRNF: Given size < 1 when get, then throw ConstraintViolationException")
    void invalid_SizeLessThan1() {

        // Arrange
        final int
                page = 1, // One-indexed
                size = 0;
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException = catchThrowableOfType(
                () -> shipmentController.findAll(givenUser, page, size), ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PAGINATION_SIZE_MIN);

        verify(shipmentService, never()).findAllByUserUuid(any(), any());
        verify(shipmentService, never()).findAll(any());
    }

    @Test
    @DisplayName("rHaNRQFcOU: Given page and size < 1 when get, then throw ConstraintViolationException")
    void invalid_PageAndSizeLessThan1() {

        // Arrange
        final int
                page = 0, // One-indexed
                size = 0;
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException = catchThrowableOfType(
                () -> shipmentController.findAll(givenUser, page, size), ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_PAGINATION_PAGE_MIN, ERROR_MESSAGE_PAGINATION_SIZE_MIN);

        verify(shipmentService, never()).findAllByUserUuid(any(), any());
        verify(shipmentService, never()).findAll(any());
    }

    @Test
    @DisplayName("vPeYGhUGTx: Given valid dto when create, then return created Shipment")
    void valid_Create() {

        // Arrange
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();

        final ShipmentStatusLessCreatedByLessUuidLessDTO givenDto = VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO;
        final ShipmentStatusLessUuidLessDTO expectedMappedDto = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO;
        final Shipment expected = VALID_SHIPMENT;

        given(shipmentService.create(expectedMappedDto)).willReturn(expected);

        // Act
        final Shipment actual = shipmentController.create(givenUser, givenDto);

        // Assert
        assertThat(actual).isEqualTo(expected);
        verify(shipmentService, times(1)).create(eq(expectedMappedDto));
    }

    @Test
    @DisplayName("ArkEamRoim: Given invalid dto when create, then throw ConstraintViolationException")
    void invalid_Create() {

        // Arrange
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();

        final ShipmentStatusLessCreatedByLessUuidLessDTO givenDto =
                VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO.toBuilder()
                        .expectedDate(LocalDate.now())
                        .orderNumber(null)
                        .to(null)
                        .build();

        // Act
        final ConstraintViolationException constraintViolationException = catchThrowableOfType(
                () -> shipmentController.create(givenUser, givenDto), ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect)
                .containsExactlyInAnyOrder(
                        ERROR_MESSAGE_SHIPMENT_EXPECTED_DATE_TOO_EARLY.replace("{days}", "3"),
                        ERROR_MESSAGE_SHIPMENT_ORDER_NUMBER_NULL,
                        ERROR_MESSAGE_SHIPMENT_TO_NULL
                );

        verify(shipmentService, never()).create(any());
    }

    @Test
    @DisplayName("PfDyAiQiZV: Given create as default login, then throw DefaultLoginNotAllowedException")
    void invalid_CreateDefaultLogin() {

        // Arrange
        final User givenUser = User.builder()
                .uuid(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_MANAGER_ROLE)
                .build();

        final ShipmentStatusLessCreatedByLessUuidLessDTO givenDto = VALID_SHIPMENT_STATUS_LESS_CREATED_BY_LESS_UUID_LESS_DTO;

        // Act
        final DefaultLoginNotAllowedException defaultLoginNotAllowedException = catchThrowableOfType(
                () -> shipmentController.create(givenUser, givenDto), DefaultLoginNotAllowedException.class);

        // Assert
        assertThat(defaultLoginNotAllowedException).isNotNull();

        verify(shipmentService, never()).create(any());
    }
}
