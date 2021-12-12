package com.lepine.transfers.data;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ItemDataTests {

    private final Pattern UUID_PATTERN = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

    private final String
        VALID_ITEM_NAME = "valid-name",
        VALID_ITEM_DESCRIPTION = "valid-description",
        VALID_ITEM_SKU = "12345678";

    @Autowired
    ItemRepo itemRepo;

    @BeforeEach
    void setUp() {
        itemRepo.deleteAll();
    }

    @Test
    @DisplayName("Context loads")
    void contextLoads() {}

    @Test
    @DisplayName("Given item, persist and retrieve")
    void saveAndRetrieve() {
        final Item save = itemRepo.save(
                Item.builder()
                        .name(VALID_ITEM_NAME)
                        .description(VALID_ITEM_DESCRIPTION)
                        .SKU(VALID_ITEM_SKU)
                        .build()
        );

        assertTrue(UUID_PATTERN.matcher(save.getUuid().toString()).matches());
        assertEquals(VALID_ITEM_NAME, save.getName());
        assertEquals(VALID_ITEM_DESCRIPTION, save.getDescription());
        assertEquals(VALID_ITEM_SKU, save.getSKU());
    }

    @Test
    @DisplayName("Given item, retrieve it by UUID")
    void findByUUID() {

        final Item save = itemRepo.save(
                Item.builder()
                        .name(VALID_ITEM_NAME)
                        .description(VALID_ITEM_DESCRIPTION)
                        .SKU(VALID_ITEM_SKU)
                        .build()
        );

        final Optional<Item> byId = itemRepo.findById(save.getUuid());
        assertTrue(byId.isPresent());

        final Item got = byId.get();
        assertTrue(UUID_PATTERN.matcher(got.getUuid().toString()).matches());
        assertEquals(VALID_ITEM_NAME, got.getName());
        assertEquals(VALID_ITEM_DESCRIPTION, got.getDescription());
        assertEquals(VALID_ITEM_SKU, got.getSKU());
    }

    @Test
    @DisplayName("Given item, delete it")
    void deleteByUUID() {

        final Item save = itemRepo.save(
                Item.builder()
                        .name(VALID_ITEM_NAME)
                        .description(VALID_ITEM_DESCRIPTION)
                        .SKU(VALID_ITEM_SKU)
                        .build()
        );

        itemRepo.deleteById(save.getUuid());

        assertEquals(0, itemRepo.count());
    }

    @Test
    @DisplayName("Given item, update it")
    void updateItem() {

        final String
                updatedName = "Updated-Name",
                updatedDescription = "Updated-Description",
                updatedSKU = "Updated-SKU";

        final Item save = itemRepo.save(
                Item.builder()
                        .name(VALID_ITEM_NAME)
                        .description(VALID_ITEM_DESCRIPTION)
                        .SKU(VALID_ITEM_SKU)
                        .build()
        );

        save.setDescription(updatedDescription);
        save.setName(updatedName);
        save.setSKU(updatedSKU);
        final Item updated = itemRepo.save(save);

        assertEquals(save.getUuid(), updated.getUuid());
        assertEquals(updatedName, updated.getName());
        assertEquals(updatedDescription, updated.getDescription());
        assertEquals(updatedSKU, updated.getSKU());
    }
}