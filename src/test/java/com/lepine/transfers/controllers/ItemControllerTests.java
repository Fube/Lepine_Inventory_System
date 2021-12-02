package com.lepine.transfers.controllers;

import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.services.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test"})
public class ItemControllerTests {

    @Autowired
    private ItemController itemController;

    @SpyBean
    private ItemRepo itemRepo; // Not mocked because mocking a Page is hell

    @SpyBean
    private ItemService itemService; // Not mocked because mocking a Page is hell

    @BeforeEach
    void setUp() {
        itemRepo.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    private void createItems(int toInsert) {
        for (int i = 0; i < toInsert; i++) {
            itemRepo.save(Item.builder()
                    .name("name"+i)
                    .description("description"+i)
                    .SKU("SKU"+i)
                    .build());
        }
    }

    @Test
    @DisplayName("Given getItems, retrieve paginated list of Items")
    void getAll() {

        // Arrange
        final int toInsert = 20;
        createItems(toInsert);

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
        createItems(toInsert);

        // Act
        final Page<Item> items = itemController.getAll(1, 1);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / 10, items.getTotalPages());
        assertEquals(0, items.getNumber());

        final List<Item> content = items.getContent();
        for (int i = 10; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
    }

    @Test
    @DisplayName("Given negative page, throws ConstrainViolationException")
    void getAllWithBadPage() {

        // Arrange
        final int toInsert = 5;
        createItems(toInsert);

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.getAll(-1, 1));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Page number cannot be less than 1",
                constraintViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Given specific page size, retrieve paginated list of Items")
    void getAllWithSize() {

        // Arrange
        final int
                toInsert = 20,
                pageSize = 10;
        createItems(toInsert);

        // Act
        final Page<Item> items = itemController.getAll(1, pageSize);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / pageSize, items.getTotalPages());
        assertEquals(items.getSize(), pageSize);

        final List<Item> content = items.getContent();
        for (int i = 0; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
    }

    @Test
    @DisplayName("Given negative page size, throws ConstrainViolationException")
    void getAllWithBadSize() {

        // Arrange
        final int toInsert = 5;
        createItems(toInsert);

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.getAll(1, -1));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Page size cannot be less than 1",
                constraintViolations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Given specific page and page size, retrieve paginated list of Items")
    void getAllWithPageAndSize() {

        // Arrange
        final int
                toInsert = 20,
                pageSize = 5,
                page = 1;
        createItems(toInsert);

        // Act
        final Page<Item> items = itemController.getAll(page, pageSize);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / pageSize, items.getTotalPages());
        assertEquals(page - 1, items.getNumber());
        assertEquals(pageSize, items.getSize());

        final List<Item> content = items.getContent();
        for (int i = 10; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
    }
}
