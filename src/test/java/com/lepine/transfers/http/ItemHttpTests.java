package com.lepine.transfers.http;

import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.services.item.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        final Item item = Item.builder()
                .uuid(UUID.randomUUID())
                .name("Item")
                .SKU("SKU")
                .description("Description")
                .build();
        given(itemService.findByUuid(item.getUuid()))
                .willReturn(Optional.empty());

        // Act
        final ResultActions resultActions = mvc.perform(get("/items/{uuid}", item.getUuid()));

        // Assert
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Item not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(itemService, times(1)).findByUuid(item.getUuid());
        verify(itemController, times(1)).getByUuid(item.getUuid());
    }
}
