package com.lepine.transfers.services;

import com.lepine.transfers.data.Item;
import com.lepine.transfers.data.ItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles({"test"})
public class ItemTests {

    @SpyBean
    private ItemRepo itemRepo;

    @Autowired
    private ItemService itemService;

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
}
