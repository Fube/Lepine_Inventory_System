package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
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

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test"})
public class ItemServiceTests {

    @SpyBean
    private ItemRepo itemRepo;

    @Autowired
    private ItemService itemService;

    @MockBean
    private SearchService<Item> searchService;

    private static Comparator<Item> itemComparator;

    static {
        itemComparator = Comparator.comparing(Item::getSKU);
    }

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
        doNothing().when(searchService).index(any(Item.class));

        // Act
        final Item saved = itemService.create(item);

        // Assert
        assertEquals(item.getName(), saved.getName());
        assertEquals(item.getSKU(), saved.getSKU());
        assertEquals(item.getDescription(), saved.getDescription());
        verify(itemRepo, times(1)).save(item);
        verify(searchService, times(1)).index(
                argThat(i -> itemComparator.compare(item, (Item)i) == 0));
    }
}
