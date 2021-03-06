package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemQuantityTuple;
import com.lepine.transfers.data.item.ItemUUIDLessDTO;
import com.lepine.transfers.exceptions.item.DuplicateSkuException;
import com.lepine.transfers.exceptions.item.ItemNotFoundException;
import com.lepine.transfers.services.item.ItemService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ValidationConfig.class, MapperConfig.class, ItemController.class})
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
                    .sku("SKU"+i)
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
        given(itemService.findAll(PageRequest.of(0, 10))).willReturn(page);

        // Act
        final Page<Item> items = itemController.getAll(1, 10);

        // Assert
        assertEquals(toInsert, items.getTotalElements());
        assertEquals(toInsert / 10, items.getTotalPages());
        assertEquals(1, items.getNumber());

        final List<Item> content = items.getContent();
        for (int i = 0; i < content.size(); i++) {
            assertEquals("name" + i, content.get(i).getName());
            assertEquals("description" + i, content.get(i).getDescription());
            assertEquals("SKU" + i, content.get(i).getSku());
        }
        verify(itemService, times(1)).findAll(any(PageRequest.class));
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
            assertEquals("SKU" + i, content.get(i).getSku());
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Page number cannot be less than 1");

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
            assertEquals("SKU" + i, content.get(i).getSku());
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Page size cannot be less than 1");
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
            assertEquals("SKU" + i, content.get(i).getSku());
        }
    }

    @Test
    @DisplayName("Given valid item dto, create new Item and send copy to SearchService")
    void createItem() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .sku("SKU")
                .build();
        given(itemService.create(any(Item.class)))
                .willReturn(itemMapper.toEntity(itemDTO));

        // Act
        final Item item = itemController.create(itemDTO);

        // Assert
        assertEquals("name", item.getName());
        assertEquals("description", item.getDescription());
        assertEquals("SKU", item.getSku());
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder("SKU is mandatory", "Name is mandatory", "Description is mandatory");

        verify(itemService, never()).create(any(Item.class));
    }

    @Test
    @DisplayName("Given item dto with all null fields, throw ConstraintViolationException")
    void createItemWithNullDTO() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name(null)
                .description(null)
                .sku(null)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.create(itemDTO));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder("SKU is mandatory", "Name is mandatory", "Description is mandatory");

        verify(itemService, never()).create(any(Item.class));
    }

    @Test
    @DisplayName("Given valid item dto and UUID, update Item and send copy to SearchService")
    void updateItem() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .sku("SKU")
                .build();
        final UUID uuid = UUID.randomUUID();
        given(itemService.update(any(Item.class)))
                .willReturn(itemMapper.toEntity(itemDTO));

        // Act
        final Item updatedItem = itemController.update(uuid, itemDTO);

        // Assert
        assertEquals("name", updatedItem.getName());
        assertEquals("description", updatedItem.getDescription());
        assertEquals("SKU", updatedItem.getSku());
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder("SKU is mandatory", "Name is mandatory", "Description is mandatory");

        verify(itemService, never()).update(any(Item.class));
    }

    @Test
    @DisplayName("Given item dto with all null fields, throw ConstraintViolationException")
    void updateItemWithNullDTO() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name(null)
                .description(null)
                .sku(null)
                .build();
        final UUID uuid = UUID.randomUUID();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> itemController.update(uuid, itemDTO));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder("SKU is mandatory", "Name is mandatory", "Description is mandatory");

        verify(itemService, never()).update(any(Item.class));
    }

    @Test
    @DisplayName("Given Item with UUID does not exist, throw NotFoundException")
    void updateItemWithNotFoundException() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .sku("SKU")
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
                .sku("SKU")
                .build();
        given(itemService.findByUuid(uuid)).willReturn(Optional.of(item));

        // Act
        final Item got = itemController.getByUuid(uuid);

        // Assert
        assertEquals(uuid, got.getUuid());
        assertEquals("name", got.getName());
        assertEquals("description", got.getDescription());
        assertEquals("SKU", got.getSku());
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

    @Test
    @DisplayName("TsXoijewqb: Given dupe SKU when create, then throw DuplicateSkuException")
    void createItemWithDuplicateSkuException() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .sku("SKU")
                .build();
        given(itemService.create(any(Item.class))).willThrow(new DuplicateSkuException(itemDTO.getSku()));

        // Act
        DuplicateSkuException exception =
                assertThrows(DuplicateSkuException.class, () -> itemController.create(itemDTO));

        // Assert
        assertThat(exception.getMessage()).contains("SKU");
        verify(itemService, times(1)).create(any(Item.class));
    }

    @Test
    @DisplayName("fTAlXEQHTq: Given dupe SKU when update, then throw DuplicateSkuException")
    void updateItemWithDuplicateSkuException() {

        // Arrange
        final ItemUUIDLessDTO itemDTO = ItemUUIDLessDTO.builder()
                .name("name")
                .description("description")
                .sku("SKU")
                .build();
        given(itemService.update(any(Item.class))).willThrow(new DuplicateSkuException(itemDTO.getSku()));

        // Act
        DuplicateSkuException exception =
                assertThrows(DuplicateSkuException.class, () -> itemController.update(UUID.randomUUID(), itemDTO));

        // Assert
        assertThat(exception.getMessage()).contains("SKU");
        verify(itemService, times(1)).update(any(Item.class));
    }

    @Test
    @DisplayName("frjNpNTaWy: Given valid pagination and temporal arguments, retrieve best selling Items")
    void retrieveBestSellingItems() {

        // Arrange
        final PageRequest pageRequest = PageRequest.of(0, 10);
        ZonedDateTime start = ZonedDateTime.now().minusDays(1);
        ZonedDateTime end = ZonedDateTime.now();
        final List<ItemQuantityTuple> items = Stream.of(
                Item.builder()
                        .uuid(UUID.randomUUID())
                        .name("name")
                        .description("description")
                        .sku("SKU")
                        .build(),
                Item.builder()
                        .uuid(UUID.randomUUID())
                        .name("name")
                        .description("description")
                        .sku("SKU")
                        .build(),
                Item.builder()
                        .uuid(UUID.randomUUID())
                        .name("name")
                        .description("description")
                        .sku("SKU")
                        .build()
        ).map(item -> new ItemQuantityTuple(item, 1L)).collect(Collectors.toList());
        final Page<ItemQuantityTuple> expectedPage = com.lepine.transfers.utils.PageUtils.createPageFor(items, pageRequest);

        given(itemService.findBestSellerForRange(start, end, pageRequest)).willReturn(expectedPage);

        // Act
        final Page<ItemQuantityTuple> got = itemController.getBestseller(1, 10, start.toString(), end.toString());

        // Assert
        assertEquals(items, got.getContent());
        verify(itemService, times(1)).findBestSellerForRange(start, end, pageRequest);
    }
}
