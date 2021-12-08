package com.lepine.transfers.controllers;

import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemUUIDLessDTO;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.services.Config;
import com.lepine.transfers.services.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {Config.class})
@ActiveProfiles({"test"})
public class ItemControllerTests {

    @Autowired
    private ItemController itemController;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @SuppressWarnings("unchecked")
    private Page<Item> createPageFor(List<Item> items, PageRequest pageRequest) {
        final Page<Item> page = mock(Page.class);
        given(page.getContent()).willReturn(items);
        given(page.getTotalElements()).willReturn((long) items.size());
        given(page.getTotalPages()).willReturn((int) Math.ceil(items.size() / pageRequest.getPageSize()));
        given(page.getNumber()).willReturn(pageRequest.getPageNumber());
        given(page.getSize()).willReturn(pageRequest.getPageSize());
        return page;
    }

    private Page<Item> createPageFor(List<Item> items) {
        return createPageFor(items, PageRequest.of(0, 10));
    }

    @BeforeEach
    void setUp() {
        reset(itemService);
    }

    @Test
    void contextLoads() {
    }

    private List<Item> createItems(int toInsert) {
        final List<Item> items = new ArrayList<>();
        for (int i = 0; i < toInsert; i++) {
            items.add(Item.builder()
                    .name("name"+i)
                    .description("description"+i)
                    .SKU("SKU"+i)
                    .build());
        }
        return items;
    }

    @Test
    @DisplayName("Given getItems, retrieve paginated list of Items")
    void getAll() {

        // Arrange
        final int toInsert = 20;
        final Page<Item> page = createPageFor(createItems(toInsert));
        given(itemService.findAll()).willReturn(page);

        // Act
        final Page<Item> items = itemController.getAll();

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / 10, items.getTotalPages());
        assertEquals(1, items.getNumber());

        final List<Item> content = items.getContent();
        for (int i = 0; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
        verify(itemService, times(1)).findAll();
    }

    @Test
    @DisplayName("Given specific page, retrieve paginated list of Items")
    void getAllWithPage() {

        // Arrange
        final int
                toInsert = 20,
                pageSize = 1;
        final Page<Item> page = createPageFor(createItems(toInsert), PageRequest.of(0, pageSize));
        given(itemService.findAll(any(PageRequest.class))).willReturn(page);

        // Act
        final Page<Item> items = itemController.getAll(1, pageSize);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / pageSize, items.getTotalPages());
        assertEquals(1, items.getNumber());

        final List<Item> content = items.getContent();
        for (int i = 10; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
        verify(itemService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Given negative page, throws ConstrainViolationException")
    void getAllWithBadPage() {

        // Arrange
        final int toInsert = 5;
        final Page<Item> page = createPageFor(createItems(toInsert));
        given(itemService.findAll(any(PageRequest.class))).willReturn(page);

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.getAll(-1, 1));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Page number cannot be less than 1",
                constraintViolations.iterator().next().getMessage());
        verify(itemService, times(0)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Given specific page size, retrieve paginated list of Items")
    void getAllWithSize() {

        // Arrange
        final int
                toInsert = 20,
                pageSize = 10,
                pageNumber = 1;
        final Page<Item> page = createPageFor(createItems(toInsert), PageRequest.of(pageNumber - 1, pageSize));
        given(itemService.findAll(any(PageRequest.class))).willReturn(page);

        // Act
        final Page<Item> items = itemController.getAll(pageNumber, pageSize);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / pageSize, items.getTotalPages());
        assertEquals(items.getSize(), pageSize);
        assertEquals(pageNumber, items.getNumber());

        final List<Item> content = items.getContent();
        for (int i = 0; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSKU());
        }
        verify(itemService, times(1)).findAll(any(PageRequest.class));
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
        given(itemService.findAll(any(PageRequest.class)))
                .willAnswer(invocation -> createPageFor(createItems(toInsert), invocation.getArgument(0)));

        // Act
        final Page<Item> items = itemController.getAll(page, pageSize);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / pageSize, items.getTotalPages());
        assertEquals(page, items.getNumber());
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
        given(itemService.create(any(Item.class)))
                .willReturn(itemMapper.toEntity(itemDTO));

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

        verify(itemService, never()).create(any(Item.class));
    }

    @Test
    @DisplayName("Given valid item dto and UUID, update Item and send copy to SearchService")
    void updateItem() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .SKU("SKU")
                .build();
        final UUID uuid = UUID.randomUUID();
        given(itemService.update(any(Item.class)))
                .willReturn(itemMapper.toEntity(itemDTO));

        // Act
        final Item updatedItem = itemController.update(uuid, itemDTO);

        // Assert
        assertEquals("name", updatedItem.getName());
        assertEquals("description", updatedItem.getDescription());
        assertEquals("SKU", updatedItem.getSKU());
        verify(itemService, times(1)).update(argThat(item -> item.getUuid().equals(uuid)));
    }

    @Test
    @DisplayName("Given empty item dto, throw ConstraintViolationException")
    void updateItemWithBadDTO() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder().build();
        final UUID uuid = UUID.randomUUID();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.update(uuid, itemDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(3, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of("SKU is mandatory", "Name is mandatory", "Description is mandatory")));
        verify(itemService, never()).update(any(Item.class));
    }

    @Test
    @DisplayName("Given item dto with all null fields, throw ConstraintViolationException")
    void updateItemWithNullDTO() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name(null)
                .description(null)
                .SKU(null)
                .build();
        final UUID uuid = UUID.randomUUID();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.update(uuid, itemDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(3, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of("SKU is mandatory", "Name is mandatory", "Description is mandatory")));

        verify(itemService, never()).update(any(Item.class));
    }

    @Test
    @DisplayName("Given Item with UUID does not exist, throw NotFoundException")
    void updateItemWithNotFoundException() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .SKU("SKU")
                .build();
        final UUID uuid = UUID.randomUUID();
        given(itemService.update(any(Item.class)))
                .willThrow(new ItemNotFoundException(uuid));

        // Act
        ItemNotFoundException exception =
                assertThrows(ItemNotFoundException.class, () -> itemController.update(uuid, itemDTO));

        // Assert
        assertEquals(format("Item with uuid %s not found", uuid), exception.getMessage());
        verify(itemService, times(1)).update(argThat(item -> item.getUuid().equals(uuid)));
    }

    @Test
    @DisplayName("Given valid UUID, delete Item")
    void deleteItem() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        doNothing().when(itemService).delete(uuid);

        // Act
        itemController.delete(uuid);

        // Assert
        verify(itemService, times(1)).delete(uuid);
    }

    @Test
    @DisplayName("Given UUID, retrieve Item")
    void retrieveItem() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Item item = Item.builder()
                .uuid(uuid)
                .name("name")
                .description("description")
                .SKU("SKU")
                .build();
        given(itemService.findByUuid(uuid)).willReturn(Optional.of(item));

        // Act
        final Item got = itemController.getByUuid(uuid);

        // Assert
        assertEquals(uuid, got.getUuid());
        assertEquals("name", got.getName());
        assertEquals("description", got.getDescription());
        assertEquals("SKU", got.getSKU());
        verify(itemService, times(1)).findByUuid(uuid);
    }

    @Test
    @DisplayName("Given non-existing UUID, throw NotFoundException")
    void retrieveItemWithNotFoundException() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        given(itemService.findByUuid(uuid)).willReturn(Optional.empty());

        // Act
        ItemNotFoundException exception =
                assertThrows(ItemNotFoundException.class, () -> itemController.getByUuid(uuid));

        // Assert
        assertEquals(format("Item with uuid %s not found", uuid), exception.getMessage());
        verify(itemService, times(1)).findByUuid(uuid);
    }
}
