package com.lepine.transfers.services;

import com.lepine.transfers.config.AlgoliaConfig;
import com.lepine.transfers.data.item.ItemSearchDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = { AlgoliaConfig.class })
public class ItemSearchServiceTests extends SearchServiceTests<ItemSearchDTO, UUID>{

    @Override
    @Test
    @DisplayName("Given item, index it")
    public void testIndex() {

        // Arrange
        final ItemSearchDTO itemSearchDTO = ItemSearchDTO.builder()
                .name("item")
                .sku("sku")
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

    @Override
    @Test
    @DisplayName("Given item UUID, delete it")
    public void testDelete() {

        // Arrange
        final UUID itemUUID = UUID.randomUUID();
        final String asString = itemUUID.toString();

        given(searchIndex.deleteObject(asString))
                .willReturn(null);

        // Act
        searchService.delete(itemUUID);

        // Assert
        verify(searchIndex, times(1)).deleteObject(asString);
    }
}
