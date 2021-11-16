package com.lepine.transfers.data;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ItemTest {

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
}