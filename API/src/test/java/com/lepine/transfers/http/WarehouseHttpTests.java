package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.warehouse.WarehouseController;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseMapper;
import com.lepine.transfers.exceptions.warehouse.DuplicateZipCodeException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static java.lang.String.format;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private final static int
            DEFAULT_PAGE = 1,
            DEFAULT_SIZE = 10;

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

    private void getAllWarehouses(final int page, final int size) throws Exception {
        // Arrange
        final Warehouse baseWarehouse = Warehouse.builder()
                .uuid(UUID.randomUUID())
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final List<Warehouse> content = Collections.nCopies(page * size, baseWarehouse);
        final PageRequest expectedPageRequest = PageRequest.of(page - 1, size);
        final Page<Warehouse> expected = createPageFor(content, expectedPageRequest);
        given(warehouseService.findAll(any(PageRequest.class)))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(get("/warehouses?page=" + page + "&size=" + size));

        // Assert
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].city").exists())
                .andExpect(jsonPath("$.content[*].zipCode").exists())
                .andExpect(jsonPath("$.content[*].province").exists())
                .andExpect(jsonPath("$.number").value(page)); // One-indexed

        verify(warehouseService, atMostOnce()).findAll(any(PageRequest.class));
    }

    private void getAllWarehousesWithPage(final int page) throws Exception {
        // Arrange
        final Warehouse baseWarehouse = Warehouse.builder()
                .uuid(UUID.randomUUID())
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final List<Warehouse> content = Collections.nCopies(page * DEFAULT_SIZE, baseWarehouse);
        final PageRequest expectedPageRequest = PageRequest.of(page - 1, DEFAULT_SIZE);
        final Page<Warehouse> expected = createPageFor(content, expectedPageRequest);
        given(warehouseService.findAll(any(PageRequest.class)))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(get("/warehouses?page=" + page));

        // Assert
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(DEFAULT_SIZE))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].city").exists())
                .andExpect(jsonPath("$.content[*].zipCode").exists())
                .andExpect(jsonPath("$.content[*].province").exists())
                .andExpect(jsonPath("$.number").value(page)); // One-indexed

        verify(warehouseService, atMostOnce()).findAll(any(PageRequest.class));
    }

    private void getAllWarehousesWithSize(final int size) throws Exception {
        // Arrange
        final Warehouse baseWarehouse = Warehouse.builder()
                .uuid(UUID.randomUUID())
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final List<Warehouse> content = Collections.nCopies(DEFAULT_PAGE * size, baseWarehouse);
        final PageRequest expectedPageRequest = PageRequest.of(DEFAULT_PAGE - 1, size);
        final Page<Warehouse> expected = createPageFor(content, expectedPageRequest);
        given(warehouseService.findAll(any(PageRequest.class)))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(get("/warehouses?size=" + size));

        // Assert
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].city").exists())
                .andExpect(jsonPath("$.content[*].zipCode").exists())
                .andExpect(jsonPath("$.content[*].province").exists())
                .andExpect(jsonPath("$.number").value(DEFAULT_PAGE)); // One-indexed

        verify(warehouseService, atMostOnce()).findAll(any(PageRequest.class));
    }

    private ResultActions createWith(final WarehouseActiveLessUUIDLessDTO given) throws Exception {
        // Arrange
        final String asString = objectMapper.writeValueAsString(given);
        final Warehouse expected = warehouseMapper.toEntity(given);

        given(warehouseService.create(given))
                .willReturn(expected);

        // Act
        return mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));
    }

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

        // Act & Assert
        createWith(given)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));

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

        // Act & Assert
        createWith(given)
                .andExpect(status().isForbidden());

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

        // Act & Assert
        createWith(given)
                .andExpect(status().isForbidden());

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
    @DisplayName("HXqfxUJWhb: Given POST on /warehouses with null zipcode as clerk, then return forbidden (403, error)")
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

    @Test
    @DisplayName("kqowXlAjJa: Given POST on /warehouses with null province as clerk, then return forbidden (403, error)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void create_AsClerk_WithNullProvince() throws Exception {

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
        perform.andExpect(status().isForbidden());

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("ILYTrmgVxh: Given POST on /warehouses with null city as salesperson, then return forbidden (403, error)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void create_AsSalesperson_WithNullCity() throws Exception {

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
    @DisplayName("jtzEASMOlT: Given POST on /warehouses with null zipcode as salesperson, then return forbidden (403, error)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void create_AsSalesperson_WithNullZipCode() throws Exception {

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

    @Test
    @DisplayName("yZRyqRbzDf: Given POST on /warehouses with null province as salesperson, then return forbidden (403, error)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void create_AsSalesperson_WithNullProvince() throws Exception {

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
        perform.andExpect(status().isForbidden());

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("UmeoFmclEc: Given POST on /warehouses with duplicate zipcode as manager, then return bad request (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_WithDuplicateZipCode() throws Exception {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        final String asString = objectMapper.writeValueAsString(given);

        given(warehouseService.create(given))
                .willThrow(new DuplicateZipCodeException(given.getZipCode()));

        // Act
        final ResultActions perform = mockMvc.perform(post("/warehouses")
                .contentType("application/json")
                .content(asString));

        // Assert
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(format(ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP, given.getZipCode())))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(warehouseService, atMostOnce()).create(given);
    }

    @Test
    @DisplayName("qmGQTuoAdg: Given POST on /warehouses with duplicate zipcode as clerk, then return forbidden (403, error)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void create_AsClerk_WithDuplicateZipCode() throws Exception {

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

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("NoCxEEEkOC: Given POST on /warehouses with duplicate zipcode as salesperson, then return forbidden (403, error)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void create_AsSalesperson_WithDuplicateZipCode() throws Exception {

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

        verify(warehouseService, never()).create(given);
    }

    @Test
    @DisplayName("JbKUwBNpKt: Given GET on /warehouses as manager, then return all warehouses paginated (200, page)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void getAll_AsManager() throws Exception {

        // Arrange
        final List<Warehouse> content = List.of(
                Warehouse.builder()
                    .uuid(UUID.randomUUID())
                    .city(VALID_CITY)
                    .zipCode(VALID_ZIP)
                    .province(VALID_PROVINCE)
                    .build()
        );
        final Page<Warehouse> expected = createPageFor(content);
        given(warehouseService.findAll(any(PageRequest.class)))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(get("/warehouses"));

        // Assert
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(content.size()))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].city").exists())
                .andExpect(jsonPath("$.content[*].zipCode").exists())
                .andExpect(jsonPath("$.content[*].province").exists())
                .andExpect(jsonPath("$.number").value(expected.getNumber() + 1)); // One-indexed

        verify(warehouseService, atMostOnce()).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("fOSzmnFBXO: Given GET on /warehouses?page=[int] as manager, then return specific page (200, page)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void getAll_AsManager_WithSpecificPage() throws Exception {
        getAllWarehousesWithPage(2);
    }

    @Test
    @DisplayName("HSVqYdGCoi: Given GET on /warehouses?size=[int] as manager, then return specific page (200, page)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void getAll_AsManager_WithSpecificSize() throws Exception {
        getAllWarehousesWithSize(100);
    }

    @Test
    @DisplayName("JGujGgOTqv: Given GET on /warehouses?page=[int]&size=[int] as manager, then return specific page (200, page)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void getAll_AsManager_WithSpecificPageAndSize() throws Exception {
        getAllWarehouses(2, 10);
    }

    @Test
    @DisplayName("PBRhsjCnmo: Given GET on /warehouses as clerk, then return all warehouses paginated (200, page)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void getAll_AsClerk() throws Exception {

        // Arrange
        final Warehouse baseWarehouse = Warehouse.builder()
                .uuid(UUID.randomUUID())
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final List<Warehouse> content = Collections.nCopies(DEFAULT_PAGE, baseWarehouse);
        final PageRequest expectedPageRequest = PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE);
        final Page<Warehouse> expected = createPageFor(content, expectedPageRequest);
        given(warehouseService.findAll(any(PageRequest.class)))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(get("/warehouses"));

        // Assert
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(DEFAULT_PAGE))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].city").exists())
                .andExpect(jsonPath("$.content[*].zipCode").exists())
                .andExpect(jsonPath("$.content[*].province").exists())
                .andExpect(jsonPath("$.number").value(1)); // One-indexed

        verify(warehouseService, atMostOnce()).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("XYNnkITxNv: Given GET on /warehouses?page=[int] as clerk, then return specific page (200, page)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void getAll_AsClerk_WithSpecificPage() throws Exception {
        getAllWarehousesWithPage(2);
    }

    @Test
    @DisplayName("pezJYptqJO: Given GET on /warehouses?size=[int] as clerk, then return specific page (200, page)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void getAll_AsClerk_WithSpecificSize() throws Exception {
        getAllWarehousesWithSize(3);
    }

    @Test
    @DisplayName("AMDRrjglKe: Given GET on /warehouses?page=[int]&size=[int] as clerk, then return specific page (200, page)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void getAll_AsClerk_WithSpecificPageAndSize() throws Exception {
        getAllWarehouses(2, 3);
    }

    @Test
    @DisplayName("JExIKDpcph: Given GET on /warehouses as salesperson, then return all warehouses paginated (200, page)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void getAll_AsSalesperson() throws Exception {

        // Arrange
        final Warehouse baseWarehouse = Warehouse.builder()
                .uuid(UUID.randomUUID())
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final List<Warehouse> content = Collections.nCopies(DEFAULT_PAGE, baseWarehouse);
        final PageRequest expectedPageRequest = PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE);
        final Page<Warehouse> expected = createPageFor(content, expectedPageRequest);
        given(warehouseService.findAll(any(PageRequest.class)))
                .willReturn(expected);

        // Act
        final ResultActions perform = mockMvc.perform(get("/warehouses"));

        // Assert
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(DEFAULT_PAGE))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].city").exists())
                .andExpect(jsonPath("$.content[*].zipCode").exists())
                .andExpect(jsonPath("$.content[*].province").exists())
                .andExpect(jsonPath("$.number").value(1)); // One-indexed

        verify(warehouseService, atMostOnce()).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("pisaONthoM: Given GET on /warehouses?page=[int] as salesperson, then return specific page (200, page)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void getAll_AsSalesperson_WithSpecificPage() throws Exception {
        getAllWarehousesWithPage(2);
    }

    @Test
    @DisplayName("HqktEthWuY: Given GET on /warehouses?size=[int] as salesperson, then return specific page (200, page)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void getAll_AsSalesperson_WithSpecificSize() throws Exception {
        getAllWarehousesWithSize(15);
    }

    @Test
    @DisplayName("XuLeAPSuhb: Given GET on /warehouses?page=[int]&size=[int] as salesperson, then return specific page (200, page)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void getAll_AsSalesperson_WithSpecificPageAndSize() throws Exception {
        getAllWarehouses(2, 3);
    }
}
