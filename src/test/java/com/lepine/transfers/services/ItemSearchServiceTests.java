package com.lepine.transfers.services;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.services.search.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ItemSearchServiceTests extends SearchServiceTests<ItemSearchDTO>{



    @Override
    @DisplayName("Given item, index it")
    public void testIndex() {

        // Arrange
        final ItemSearchDTO itemSearchDTO = new ItemSearchDTO()
                .name("item")
                .sku("sku");
        given(searchIndex.saveObject(itemSearchDTO))
                .willReturn(null);

        // Act
        searchService.index(itemSearchDTO);

        // Assert
        // Nothing to assert really, basically just hoping it does not throw an exception
        verify(searchIndex).saveObject(itemSearchDTO);
        verify(searchIndex, times(1)).saveObject(itemSearchDTO);
    }
}
