package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.item.ItemSearchDTO;
import com.lepine.transfers.services.item.ItemService;
import com.lepine.transfers.services.search.SearchService;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { Config.class })
@ActiveProfiles({"test"})
public class ItemServiceTests {

    @SpyBean
    private ItemRepo itemRepo;

    @Autowired
    private ItemService itemService;

    @MockBean
    private SearchService<ItemSearchDTO, UUID> searchService;

    @Test
    void contextLoads(){}

    @BeforeEach
    void setup() {
        itemRepo.deleteAllInBatch();
    }


    private void seedRepo(int num) {
        for (int i = 0; i < num; i++) {
            itemRepo.save(
                    Item.builder()
                            .name("name"+i)
                            .SKU("SKU"+i)
                            .description("description"+i)
                            .build());
        }
    }

    @Test
    @DisplayName("Given findAll with no arguments, retrieve paginated list of items")
    void findAllNoPageRequest() {

        // Arrange
        final int num = 100;
        seedRepo(num);

        // Act
        final Page<Item> all = itemService.findAll();

        // Assert
        assertEquals(num, all.getTotalElements());
        assertEquals(num / 10, all.getContent().size());
        all.getContent().forEach(item -> {
            assertTrue(item.getName().startsWith("name"));
            assertTrue(item.getSKU().startsWith("SKU"));
            assertTrue(item.getDescription().startsWith("description"));
        });
        verify(itemRepo, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Given findAll with page request, retrieve paginated list of items")
    void findAllWithPageRequest() {

        // Arrange
        final int num = 100;
        seedRepo(num);
        final int page = 1;
        final int size = 10;
        final PageRequest pageRequest = PageRequest.of(page, size);

        // Act
        final Page<Item> all = itemService.findAll(pageRequest);

        // Assert
        assertEquals(num, all.getTotalElements());
        assertEquals(size, all.getContent().size());
        all.getContent().forEach(item -> {
            assertTrue(item.getName().startsWith("name"));
            assertTrue(item.getSKU().startsWith("SKU"));
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
                .SKU("SKU")
                .description("description")
                .build();
        doNothing().when(searchService).index(any(ItemSearchDTO.class));

        // Act
        final Item saved = itemService.create(item);

        // Assert
        assertEquals(item.getName(), saved.getName());
        assertEquals(item.getSKU(), saved.getSKU());
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
                .SKU("SKU")
                .description("description")
                .build();
        doNothing().when(searchService).index(any(ItemSearchDTO.class));

        // Act
        final Item saved = itemService.update(item);

        // Assert
        assertEquals(item.getName(), saved.getName());
        assertEquals(item.getSKU(), saved.getSKU());
        assertEquals(item.getDescription(), saved.getDescription());
        verify(itemRepo, times(1)).save(item);
        verify(searchService, times(1))
                .index(any(ItemSearchDTO.class));
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
    }
}
