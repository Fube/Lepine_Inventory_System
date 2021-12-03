package com.lepine.transfers.services;

import com.lepine.transfers.data.item.ItemSearchDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ItemSearchServiceTests extends SearchServiceTests<ItemSearchDTO>{

    @Override
    @Test
    @DisplayName("Given item, index it")
    public void testIndex() {

        // Arrange
        final ItemSearchDTO itemSearchDTO = ItemSearchDTO.builder()
                .name("item")
                .SKU("sku")
                .build();

        given(searchIndex.saveObject(itemSearchDTO))
                .willReturn(null);
        given(searchClient.initIndex("items", ItemSearchDTO.class))
                .willReturn(searchIndex);

        // Act
        searchService.index(itemSearchDTO);

        // Assert
        // Nothing to assert really, basically just hoping it does not throw an exception
        verify(searchIndex).saveObject(itemSearchDTO);
        verify(searchIndex, times(1)).saveObject(itemSearchDTO);
    }
}
