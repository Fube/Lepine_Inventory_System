package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.events.item.ItemDeleteEvent;
import com.lepine.transfers.events.item.ItemUpdateEvent;
import com.lepine.transfers.exceptions.item.DuplicateSkuException;
import com.lepine.transfers.services.item.ItemServiceImpl;
import com.lepine.transfers.services.search.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {MapperConfig.class, ItemServiceImpl.class})
@ActiveProfiles({"test"})
public class ItemServiceTests {

    private final static String ERROR_FORMAT_DUPLICATE_SKU = "Item with SKU %s already exists";

    @Autowired
    private ItemServiceImpl itemService;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private SearchService<ItemSearchDTO, UUID> searchService;

    @Test
    void contextLoads(){}

    @BeforeEach
    void setup() {
        itemService.setApplicationEventPublisher(applicationEventPublisher);
        reset(itemRepo, applicationEventPublisher, searchService);
        itemRepo.deleteAllInBatch();
    }


    private static List<Item> generateItems(int num) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            items.add(Item.builder()
                            .name("name"+i)
                            .sku("SKU"+i)
                            .description("description"+i)
                            .build());
        }
        return items;
    }

    @Test
    @DisplayName("Given findAll with no arguments, retrieve paginated list of items")
    void findAllNoPageRequest() {

        // Arrange
        final int num = 100;
        final Page<Item> pageFor = createPageFor(generateItems(num), PageRequest.of(0, 10));
        given(itemRepo.findAll(any(PageRequest.class))).willReturn(pageFor);

        // Act
        final Page<Item> all = itemService.findAll();

        // Assert
        assertEquals(num, all.getTotalElements());
        assertEquals(num / 10, all.getContent().size());
        all.getContent().forEach(item -> {
            assertTrue(item.getName().startsWith("name"));
            assertTrue(item.getSku().startsWith("SKU"));
            assertTrue(item.getDescription().startsWith("description"));
        });
        verify(itemRepo, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Given findAll with page request, retrieve paginated list of items")
    void findAllWithPageRequest() {

        // Arrange
        final int num = 100;
        final int page = 1;
        final int size = 10;
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Item> pageFor = createPageFor(generateItems(num), pageRequest);
        given(itemRepo.findAll(pageRequest)).willReturn(pageFor);

        // Act
        final Page<Item> all = itemService.findAll(pageRequest);

        // Assert
        assertEquals(num, all.getTotalElements());
        assertEquals(size, all.getContent().size());
        all.getContent().forEach(item -> {
            assertTrue(item.getName().startsWith("name"));
            assertTrue(item.getSku().startsWith("SKU"));
            assertTrue(item.getDescription().startsWith("description"));
        });
        verify(itemRepo, times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("Given valid item, create item and send copy to SearchService")
    void saveItem() {

        // Arrange
        final Item item = Item.builder()
                .name("name")
                .sku("SKU")
                .description("description")
                .build();
        doNothing().when(searchService).index(any(ItemSearchDTO.class));
        given(itemRepo.save(item)).willReturn(item);

        // Act
        final Item saved = itemService.create(item);

        // Assert
        assertEquals(item.getName(), saved.getName());
        assertEquals(item.getSku(), saved.getSku());
        assertEquals(item.getDescription(), saved.getDescription());
        verify(itemRepo, times(1)).save(item);
        verify(searchService, times(1))
                .index(any(ItemSearchDTO.class));
    }

    @Test
    @DisplayName("Given valid item, update item and send copy to SearchService")
    void updateItem() {

        // Arrange
        final Item item = Item.builder()
                .name("name")
                .sku("SKU")
                .description("description")
                .build();

        doNothing().when(searchService).index(any(ItemSearchDTO.class));
        given(itemRepo.save(item)).willReturn(item);

        // Act
        final Item saved = itemService.update(item);

        // Assert
        assertEquals(item.getName(), saved.getName());
        assertEquals(item.getSku(), saved.getSku());
        assertEquals(item.getDescription(), saved.getDescription());

        verify(itemRepo, times(1)).save(item);
        verify(searchService, times(1))
                .index(any(ItemSearchDTO.class));
        verify(applicationEventPublisher, times(1)).publishEvent(any(ItemUpdateEvent.class));
    }

    @Test
    @DisplayName("Given valid item UUID, delete item and send copy to SearchService")
    void deleteItem() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        given(itemRepo.deleteByUuid(uuid)).willReturn(1);
        doNothing().when(searchService).delete(uuid);

        // Act
        itemService.delete(uuid);

        // Assert
        verify(searchService, times(1))
                .delete(uuid);
        verify(applicationEventPublisher, times(1)).publishEvent(any(ItemDeleteEvent.class));
    }

    @Test
    @DisplayName("Given non-existent item UUID, do not invoke SearchService::delete")
    void deleteNonExistentItem() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        given(itemRepo.deleteByUuid(uuid)).willReturn(0);
        doNothing().when(searchService).delete(uuid);

        // Act
        itemService.delete(uuid);

        // Assert
        verify(searchService, never())
                .delete(uuid);
        verify(applicationEventPublisher, times(1)).publishEvent(any(ItemDeleteEvent.class));
    }

    @Test
    @DisplayName("Given valid item UUID, retrieve item")
    void getByUUID() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Item item = Item.builder()
                .uuid(uuid)
                .name("name")
                .sku("SKU")
                .description("description")
                .build();
        given(itemRepo.findById(uuid)).willReturn(Optional.of(item));

        // Act
        final Optional<Item> retrieved = itemService.findByUuid(uuid);

        // Assert
        assertTrue(retrieved.isPresent());
        final Item retrievedItem = retrieved.get();
        assertEquals(item.getName(), retrievedItem.getName());
        assertEquals(item.getSku(), retrievedItem.getSku());
        assertEquals(item.getDescription(), retrievedItem.getDescription());
        verify(itemRepo, times(1)).findById(uuid);
    }

    @Test
    @DisplayName("Given non-existent item UUID, return empty optional")
    void getNonExistentItem() {

        // Arrange
        final UUID uuid = UUID.randomUUID();
        given(itemRepo.findById(uuid)).willReturn(Optional.empty());

        // Act
        final Optional<Item> retrieved = itemService.findByUuid(uuid);

        // Assert
        assertFalse(retrieved.isPresent());
        verify(itemRepo, times(1)).findById(uuid);
    }

    @Test
    @DisplayName("psoOxrYKmw: Given duplicate SKU when create, then throw DuplicateSkuException")
    void createDuplicateSku() {

        // Arrange
        final Item item = Item.builder()
                .name("name")
                .sku("SKU")
                .description("description")
                .build();
        given(itemRepo.findBySkuIgnoreCase(item.getSku())).willReturn(Optional.of(item));

        // Act
        final Throwable throwable = assertThrows(DuplicateSkuException.class, () -> itemService.create(item));

        // Assert
        assertEquals(format(ERROR_FORMAT_DUPLICATE_SKU, item.getSku()), throwable.getMessage());
        verify(itemRepo, times(1)).findBySkuIgnoreCase(item.getSku());
        verify(itemRepo, never()).save(item);
    }

    @Test
    @DisplayName("xTyRBjnPWY: Given duplicate SKU with different case when create, then throw DuplicateSkuException")
    void createDuplicateSkuDifferentCase() {

        // Arrange
        final String originalSku = "sku";
        final Item item = Item.builder()
                .name("name")
                .sku(originalSku.toUpperCase())
                .description("description")
                .build();
        given(itemRepo.findBySkuIgnoreCase(
                argThat(n -> n.equalsIgnoreCase(originalSku)
        ))).willReturn(Optional.of(item));

        // Act
        final Throwable throwable = assertThrows(DuplicateSkuException.class, () -> itemService.create(item));

        // Assert
        assertEquals(format(ERROR_FORMAT_DUPLICATE_SKU, item.getSku()), throwable.getMessage());
        verify(itemRepo, times(1)).findBySkuIgnoreCase(item.getSku());
        verify(itemRepo, never()).save(item);
    }

    @Test
    @DisplayName("vSdVkauYBE: Given duplicate SKU when update, then throw DuplicateSkuException")
    void updateDuplicateSku() {

        // Arrange
        final Item item = Item.builder()
                .name("name")
                .sku("SKU")
                .description("description")
                .build();
        given(itemRepo.findBySkuIgnoreCase(item.getSku())).willReturn(Optional.of(item.toBuilder().uuid(UUID.randomUUID()).build()));

        // Act
        final Throwable throwable = assertThrows(DuplicateSkuException.class, () -> itemService.update(item));

        // Assert
        assertEquals(format(ERROR_FORMAT_DUPLICATE_SKU, item.getSku()), throwable.getMessage());
        verify(itemRepo, times(1)).findBySkuIgnoreCase(item.getSku());
        verify(itemRepo, never()).save(item);
    }
}
