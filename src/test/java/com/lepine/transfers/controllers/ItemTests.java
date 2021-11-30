package com.lepine.transfers.controllers;

import com.lepine.transfers.data.Item;
import com.lepine.transfers.data.ItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({"test"})
public class ItemTests {

    @Autowired
    private ItemController itemController;

    @SpyBean
    private ItemRepo itemRepo; // Not mocked because mocking a Page is hell

    @BeforeEach
    void setUp() {
        itemRepo.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Given getItems, retrieve paginated list of Items")
    void getAll() {

        // Arrange
        final int toInsert = 20;
        for (int i = 0; i < toInsert; i++) {
            itemRepo.save(Item.builder()
                    .name("name"+i)
                    .description("description"+i)
                    .SKU("SKU"+i)
                    .build());
        }

        // Act
        final Page<Item> items = itemController.getAll();

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / 10, items.getTotalPages());

        final List<Item> content = items.getContent();
        for (int i = 0; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
    }

    @Test
    @DisplayName("Given specific page, retrieve paginated list of Items")
    void getAllWithPage() {

        // Arrange
        final int toInsert = 20;
        for (int i = 0; i < toInsert; i++) {
            itemRepo.save(Item.builder()
                    .name("name"+i)
                    .description("description"+i)
                    .SKU("SKU"+i)
                    .build());
        }

        // Act
        final Page<Item> items = itemController.getAll(1);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / 10, items.getTotalPages());
        assertEquals(items.getNumber(), 1);

        final List<Item> content = items.getContent();
        for (int i = 10; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
    }
}
