package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.warehouse.WarehouseController;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseMapper;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Locale;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { WarehouseController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WarehouseHttpTests {

    private final static String
            VALID_CITY = "City",
            VALID_ZIP = "A1B2C3",
            VALID_PROVINCE = "Province",
            ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP = "Zipcode %s already in use",
            ERROR_FORMAT_MESSAGE_WAREHOUSE_NOT_FOUND = "Warehouse with uuid %s not found";
    private final static UUID
            VALID_UUID = UUID.randomUUID();

    private String
            ERROR_MESSAGE_CITY_NOT_NULL,
            ERROR_MESSAGE_CITY_NOT_BLANK,
            ERROR_MESSAGE_ZIP_NOT_NULL,
            ERROR_MESSAGE_ZIP_NOT_BLANK,
            ERROR_MESSAGE_PROVINCE_NOT_NULL,
            ERROR_MESSAGE_PROVINCE_NOT_BLANK;

    @BeforeAll
    void bSetup(){
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ERROR_MESSAGE_CITY_NOT_NULL = w.getMessage("warehouse.city.not_null");
        ERROR_MESSAGE_CITY_NOT_BLANK = w.getMessage("warehouse.city.not_blank");
        ERROR_MESSAGE_ZIP_NOT_NULL = w.getMessage("warehouse.zipcode.not_null");
        ERROR_MESSAGE_ZIP_NOT_BLANK = w.getMessage("warehouse.zipcode.not_blank");
        ERROR_MESSAGE_PROVINCE_NOT_NULL = w.getMessage("warehouse.province.not_null");
        ERROR_MESSAGE_PROVINCE_NOT_BLANK = w.getMessage("warehouse.province.not_blank");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WarehouseController warehouseController;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private WarehouseService warehouseService;


    @Test
    void contextLoads() {}

    @Test
    @DisplayName("FjWippwTSO: Given POST on /warehouses with valid warehouse dto as manager, then return created (201, warehouse)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        final Warehouse expected = warehouseMapper.toEntity(given);

        given(warehouseService.create(given))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(warehouseService, atMostOnce()).create(given);
    }

    @Test
    @DisplayName("ecFdSbPbFA: Given POST on /warehouses with valid warehouse dto as clerk, then return forbidden (403, error)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void create_AsClerk() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isForbidden());

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("gYhCSDHEzm: Given POST on /warehouses with valid warehouse dto as salesperson, then return forbidden (403, error)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void create_AsSalesperson() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isForbidden());

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("koIqErRiKw: GivenPOST on /warehouses with blank city as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithBlankCity() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city("")
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.city").isArray())
                .andExpect(jsonPath("$.errors.city[*]")
                        .value(containsInAnyOrder(ERROR_MESSAGE_CITY_NOT_BLANK)));

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("rFfxEvzkfp: Given POST on /warehouses with null city as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithNullCity() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(null)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.city").isArray())
                .andExpect(jsonPath("$.errors.city[*]")
                        .value(containsInAnyOrder(ERROR_MESSAGE_CITY_NOT_BLANK, ERROR_MESSAGE_CITY_NOT_NULL)));

        verify(warehouseService, never()).create(given);
    }


    @Test
    @DisplayName("CqjhRZtJkp: Given POST on /warehouses with blank zipcode as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithBlankZipCode() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode("")
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.zipCode").isArray())
                .andExpect(jsonPath("$.errors.zipCode[*]")
                        .value(containsInAnyOrder(ERROR_MESSAGE_ZIP_NOT_BLANK)));

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("qdXhJmQpYG: Given POST on /warehouses with null zipcode as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithNullZipCode() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(null)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.zipCode").isArray())
                .andExpect(jsonPath("$.errors.zipCode[*]")
                        .value(containsInAnyOrder(ERROR_MESSAGE_ZIP_NOT_BLANK, ERROR_MESSAGE_ZIP_NOT_NULL)));

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("qUPxDwDfFt: Given POST on /warehouses with blank province as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithBlankProvince() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province("")
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.province").isArray())
                .andExpect(jsonPath("$.errors.province[*]")
                        .value(containsInAnyOrder(ERROR_MESSAGE_PROVINCE_NOT_BLANK)));

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("LNUOMugqCh: Given POST on /warehouses with null province as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithNullProvince() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(null)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.errors.province").isArray())
                .andExpect(jsonPath("$.errors.province[*]")
                        .value(containsInAnyOrder(ERROR_MESSAGE_PROVINCE_NOT_BLANK, ERROR_MESSAGE_PROVINCE_NOT_NULL)));

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("qdXhJmQpYG: Given POST on /warehouses with null city as clerk, then return forbidden (403, error)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void create_AsClerk_WithNullCity() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(null)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isForbidden());

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("HXqfxUJWhb: Given POST on /warehouses with null zip code as clerk, then return forbidden (403, error)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void create_AsClerk_WithNullZipCode() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(null)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isForbidden());

        verify(warehouseService, never()).create(given);
    }
}
