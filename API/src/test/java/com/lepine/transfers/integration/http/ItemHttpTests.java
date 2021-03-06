package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemUUIDLessDTO;
import com.lepine.transfers.exceptions.item.DuplicateSkuException;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.utils.matchers.ItemMatcher;
import com.lepine.transfers.services.item.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;


import static com.lepine.transfers.utils.PageUtils.createPageFor;
import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { ItemController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class})
@ActiveProfiles("test")
public class ItemHttpTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @SpyBean
    private ItemController itemController;

    @MockBean
    private ItemService itemService;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given GET on /items, returns 200 OK and a paginated list of items")
    @WithMockUser(username = "test-user")
    void getItems() throws Exception {
        // Arrange
        final int
                LEN = 100,
                PER_PAGE = 10;
        final List<Item> items = new ArrayList<>();
        for (int i = 0; i < LEN; i++) {
            items.add(Item.builder()
                    .uuid(UUID.randomUUID())
                    .name("Item " + i)
                    .sku("SKU " + i)
                    .description("Description " + i)
                    .build());
        }
        final Page<Item> pageFor = createPageFor(items);
        given(itemService.findAll(PageRequest.of(0, PER_PAGE)))
                .willReturn(pageFor);

        // Act
        final ResultActions resultActions = mvc.perform(get("/items"));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(LEN / PER_PAGE))
                .andExpect(jsonPath("$.totalElements").value(LEN))
                .andExpect(jsonPath("$.totalPages").value(LEN / PER_PAGE));

        verify(itemService, times(1)).findAll(PageRequest.of(0, PER_PAGE));
        verify(itemController, times(1)).getAll(1, 10);
    }

    @Test
    @DisplayName("Given GET on /items/{uuid}, returns 200 OK and the item")
    @WithMockUser(username = "test-user")
    void getItem() throws Exception {
        // Arrange
        final Item item = Item.builder()
                .uuid(UUID.randomUUID())
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        given(itemService.findByUuid(item.getUuid()))
                .willReturn(Optional.of(item));

        // Act
        final ResultActions resultActions = mvc.perform(get("/items/{uuid}", item.getUuid()));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").value(item.getUuid().toString()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.sku").value(item.getSku()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).findByUuid(item.getUuid());
        verify(itemController, times(1)).getByUuid(item.getUuid());
    }

    @Test
    @DisplayName("Given GET on /items/{uuid}, returns 404 NOT FOUND if the item does not exist")
    @WithMockUser(username = "test-user")
    void getItemNotFound() throws Exception {
        // Arrange
        final UUID uuid = UUID.randomUUID();
        given(itemService.findByUuid(uuid))
                .willReturn(Optional.empty());

        // Act
        final ResultActions resultActions = mvc.perform(get("/items/{uuid}", uuid));

        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(format("Item with uuid %s not found", uuid)))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(1)).findByUuid(uuid);
        verify(itemController, times(1)).getByUuid(uuid);
    }


    @Test
    @DisplayName("IbPLePcvkQ: Given POST on /items as MANAGER, returns 201 CREATED and the item")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void postItem_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        final ArgumentMatcher<Item> matcher = new ItemMatcher(item);
        given(itemService.create(argThat(matcher)))
                .willReturn(item);

        // Act
        final ResultActions resultActions = mvc.perform(post("/items")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").value(item.getUuid().toString()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.sku").value(item.getSku()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).create(argThat(matcher));
        verify(itemController, times(1)).create(itemUUIDLessDTO);
    }

    @Test
    @DisplayName("OiCLZKJPjG: Given POST on /items as anyone but MANAGER, returns 403 FORBIDDEN")
    @WithMockUser(username = "test-user", roles = {"CLERK"})
    void postItem_AsNotManager() throws Exception {

        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        final ArgumentMatcher<Item> matcher = new ItemMatcher(item);

        // Act
        final ResultActions resultActions = mvc.perform(post("/items")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(itemService, times(0)).create(argThat(matcher));
        verify(itemController, times(0)).create(itemUUIDLessDTO);
    }

    @Test
    @DisplayName("AKNDXBYUUJ: Given POST on /items as MANAGER, returns 400 BAD REQUEST if the item is invalid")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void postItemInvalid_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .description("")
                .name("")
                .sku("")
                .build();

        // Act
        final ResultActions resultActions = mvc.perform(post("/items")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemUUIDLessDTO)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.name[0]")
                        .value(messageSource.getMessage("item.name.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.description").exists())
                .andExpect(jsonPath("$.errors.description[0]")
                        .value(messageSource.getMessage("item.description.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.sku[0]")
                        .value(messageSource.getMessage("item.sku.not_blank", null, Locale.getDefault())));

        verify(itemService, times(0)).create(any(Item.class));
        verify(itemController, times(0)).create(itemUUIDLessDTO);
    }

    @Test
    @DisplayName("PBUHnvoTjA: Given POST on /items as anyone but MANAGER with invalid item, returns 403 FORBIDDEN")
    @WithMockUser(username = "test-user", roles = {"CLERK"})
    void postItemInvalid_AsNotManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .description("")
                .name("")
                .sku("")
                .build();

        // Act
        final ResultActions resultActions = mvc.perform(post("/items")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(itemService, times(0)).create(any(Item.class));
        verify(itemController, times(0)).create(itemUUIDLessDTO);
    }

    @Test
    @DisplayName("HCrFDGHZEW: Given PUT on /items/{uuid}, returns 200 OK and the item")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void putItem_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        final ArgumentMatcher<Item> matcher = new ItemMatcher(item);
        given(itemService.update(argThat(matcher)))
                .willReturn(item);

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", item.getUuid())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").value(item.getUuid().toString()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.sku").value(item.getSku()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).update(argThat(matcher));
        verify(itemController, times(1)).update(item.getUuid(), itemUUIDLessDTO);
    }

    @Test
    @DisplayName("APdJIdwsvE: Given put on /items/{uuid} as anyone but MANAGER, returns 403 FORBIDDEN")
    @WithMockUser(username = "test-user", roles = {"CLERK"})
    void putItem_AsNotManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", item.getUuid())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(itemService, times(0)).update(any(Item.class));
        verify(itemController, times(0)).update(item.getUuid(), itemUUIDLessDTO);
    }

    @Test
    @DisplayName("FbSHvDjKod: Given PUT on /items/{uuid} as MANAGER, returns 400 BAD REQUEST if the item is invalid")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void putItemInvalid_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .description("")
                .name("")
                .sku("")
                .build();

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemUUIDLessDTO)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.name[0]")
                        .value(messageSource.getMessage("item.name.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.description").exists())
                .andExpect(jsonPath("$.errors.description[0]")
                        .value(messageSource.getMessage("item.description.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.sku[0]")
                        .value(messageSource.getMessage("item.sku.not_blank", null, Locale.getDefault())));

        verify(itemService, times(0)).update(any(Item.class));
        verify(itemController, times(0)).update(any(UUID.class), any(ItemUUIDLessDTO.class));
    }

    @Test
    @DisplayName("ioYFTsZugU: Given PUT on /items/{uuid} as anyone but MANAGER, returns 403 FORBIDDEN")
    @WithMockUser(username = "test-user", roles = {"CLERK"})
    void putItemInvalid_AsNotManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .description("")
                .name("")
                .sku("")
                .build();

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(itemService, times(0)).update(any(Item.class));
        verify(itemController, times(0)).update(any(UUID.class), any(ItemUUIDLessDTO.class));
    }

    @Test
    @DisplayName("kUvGTJAiWh: Given PUT on /items/{uuid} as MANAGER, returns 404 NOT FOUND if the item does not exist")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void putItem_NotFound_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        given(itemService.update(any(Item.class)))
                .willThrow(new ItemNotFoundException(item.getUuid()));

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", item.getUuid())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(format("Item with uuid %s not found", item.getUuid())))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(1)).update(any(Item.class));
        verify(itemController, times(1)).update(any(UUID.class), any(ItemUUIDLessDTO.class));
    }

    @Test
    @DisplayName("ixZWXNEZjM: Given PUT /items/{uuid} as anyone but manager, returns 403 FORBIDDEN even if the item does not exists")
    @WithMockUser(username = "test-user", roles = {"CLERK"})
    void putItem_NotFound_AsNotManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        given(itemService.update(any(Item.class)))
                .willThrow(new ItemNotFoundException(item.getUuid()));

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", item.getUuid())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(itemService, times(0)).update(any(Item.class));
        verify(itemController, times(0)).update(any(UUID.class), any(ItemUUIDLessDTO.class));
    }

    @Test
    @DisplayName("WdywCQcVII: Given DELETE on /items/{uuid} as MANAGER, returns 204 NO CONTENT")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void deleteItem_AsManager() throws Exception {
        // Arrange
        final UUID uuid = UUID.randomUUID();
        doNothing().when(itemService).delete(uuid);

        // Act
        final ResultActions resultActions = mvc.perform(delete("/items/{uuid}", uuid));

        // Assert
        resultActions
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).delete(uuid);
        verify(itemController, times(1)).delete(uuid);
    }

    @Test
    @DisplayName("KCJjpJiutY: Given DELETE on /items/{uuid} as anyone but MANAGER, returns 403 FORBIDDEN")
    @WithMockUser(username = "test-user", roles = {"CLERK"})
    void deleteItem_NotManager() throws Exception {
        // Arrange
        final UUID uuid = UUID.randomUUID();
        doNothing().when(itemService).delete(uuid);

        // Act
        final ResultActions resultActions = mvc.perform(delete("/items/{uuid}", uuid));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(itemService, times(0)).delete(uuid);
        verify(itemController, times(0)).delete(uuid);
    }

    @Test
    @DisplayName("TpGsYPSFkO: Given POST on /items with dupe SKU as Manager, returns 400 BAD REQUEST")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void postItem_DupeSKU_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        given(itemService.create(any(Item.class))).willThrow(new DuplicateSkuException(item.getSku()));

        // Act
        final ResultActions resultActions = mvc.perform(post("/items")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString(item.getSku())))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(1)).create(any(Item.class));
        verify(itemController, times(1)).create(any(ItemUUIDLessDTO.class));
    }

    @Test
    @DisplayName("oBYMHzFVov: Given PUT on /items/{uuid} with dupe SKU as Manager, returns 400 BAD REQUEST")
    @WithMockUser(username = "test-user", roles = {"MANAGER"})
    void putItem_DupeSKU_AsManager() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .sku("SKU")
                .description("Description")
                .build();
        final Item item = itemMapper.toEntity(itemUUIDLessDTO);
        given(itemService.update(any(Item.class))).willThrow(new DuplicateSkuException(item.getSku()));

        // Act
        final ResultActions resultActions = mvc.perform(put("/items/{uuid}", item.getUuid())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString(item.getSku())))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(1)).update(any(Item.class));
        verify(itemController, times(1)).update(any(UUID.class), any(ItemUUIDLessDTO.class));
    }
}
