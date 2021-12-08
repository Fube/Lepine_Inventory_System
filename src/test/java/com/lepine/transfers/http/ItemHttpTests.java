package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemUUIDLessDTO;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.services.item.ItemService;
import helpers.matchers.ItemMatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static helpers.PageHelpers.createPageFor;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { ItemController.class })
@ContextConfiguration(classes = { Config.class })
@ActiveProfiles("test")
public class ItemHttpTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @SpyBean
    private ItemController itemController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given GET on /items, returns 200 OK and a paginated list of items")
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
                    .SKU("SKU " + i)
                    .description("Description " + i)
                    .build());
        }
        final Page<Item> pageFor = createPageFor(items);
        given(itemService.findAll())
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

        verify(itemService, times(1)).findAll();
        verify(itemController, times(1)).getAll();
    }

    @Test
    @DisplayName("Given GET on /items/{uuid}, returns 200 OK and the item")
    void getItem() throws Exception {
        // Arrange
        final Item item = Item.builder()
                .uuid(UUID.randomUUID())
                .name("Item")
                .SKU("SKU")
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
                .andExpect(jsonPath("$.sku").value(item.getSKU()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).findByUuid(item.getUuid());
        verify(itemController, times(1)).getByUuid(item.getUuid());
    }

    @Test
    @DisplayName("Given GET on /items/{uuid}, returns 404 NOT FOUND if the item does not exist")
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
    @DisplayName("Given POST on /items, returns 201 CREATED and the item")
    void postItem() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .SKU("SKU")
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
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").value(item.getUuid().toString()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.sku").value(item.getSKU()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).create(argThat(matcher));
        verify(itemController, times(1)).create(itemUUIDLessDTO);
    }

    @Test
    @DisplayName("Given POST on /items, returns 400 BAD REQUEST if the item is invalid")
    void postItemInvalid() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .description("")
                .name("")
                .SKU("")
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
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(0)).create(any(Item.class));
        verify(itemController, times(0)).create(itemUUIDLessDTO);
    }

    @Test
    @DisplayName("Given PUT on /items/{uuid}, returns 200 OK and the item")
    void putItem() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .SKU("SKU")
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
                .andExpect(jsonPath("$.sku").value(item.getSKU()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).update(argThat(matcher));
        verify(itemController, times(1)).update(item.getUuid(), itemUUIDLessDTO);
    }

    @Test
    @DisplayName("Given PUT on /items/{uuid}, returns 400 BAD REQUEST if the item is invalid")
    void putItemInvalid() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .description("")
                .name("")
                .SKU("")
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
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(0)).update(any(Item.class));
        verify(itemController, times(0)).update(any(UUID.class), any(ItemUUIDLessDTO.class));
    }

    @Test
    @DisplayName("Given PUT on /items/{uuid}, returns 404 NOT FOUND if the item does not exist")
    void putItemNotFound() throws Exception {
        // Arrange
        final ItemUUIDLessDTO itemUUIDLessDTO = ItemUUIDLessDTO.builder()
                .name("Item")
                .SKU("SKU")
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
    @DisplayName("Given DELETE on /items/{uuid}, returns 204 NO CONTENT")
    void deleteItem() throws Exception {
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
}
