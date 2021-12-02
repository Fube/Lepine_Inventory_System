package com.lepine.transfers.controllers;

import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.item.ItemUUIDLessDTO;
import com.lepine.transfers.services.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        itemRepo.deleteAllInBatch();
        reset(itemRepo);
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
        verify(itemRepo, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Given specific page, retrieve paginated list of Items")
    void getAllWithPage() {

        // Arrange
        final int
                toInsert = 20,
                pageSize = 1;
        createItems(toInsert);

        // Act
        final Page<Item> items = itemController.getAll(1, pageSize);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / pageSize, items.getTotalPages());
        assertEquals(0, items.getNumber());

        final List<Item> content = items.getContent();
        for (int i = 10; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
        verify(itemRepo, times(1)).findAll(any(PageRequest.class));
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
        verify(itemRepo, times(0)).findAll(any(PageRequest.class));
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
        verify(itemRepo, times(1)).findAll(any(PageRequest.class));
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

    @Test
    @DisplayName("Given valid item dto, create new Item and send copy to SearchService")
    void createItem() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .SKU("SKU")
                .build();

        // Act
        final Item item = itemController.create(itemDTO);

        // Assert
        assertEquals("name", item.getName());
        assertEquals("description", item.getDescription());
        assertEquals("SKU", item.getSKU());
        verify(itemService, times(1)).create(any(Item.class));
    }

    @Test
    @DisplayName("Given empty item dto, throw ConstraintViolationException")
    void createItemWithBadDTO() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder().build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.create(itemDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(3, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of("SKU is mandatory", "Name is mandatory", "Description is mandatory")));
        verify(itemService, never()).create(any(Item.class));
    }

    @Test
    @DisplayName("Given item dto with all null fields, throw ConstraintViolationException")
    void createItemWithNullDTO() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name(null)
                .description(null)
                .SKU(null)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.create(itemDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(3, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of("SKU is mandatory", "Name is mandatory", "Description is mandatory")));

        verify(itemRepo, never()).save(any(Item.class));
    }
}
