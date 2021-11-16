package com.lepine.transfers.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles({"test"})
class ItemTest {

    @Autowired
    ItemRepo itemRepo;

    @BeforeEach
    void setUp() {
        itemRepo.deleteAll();
    }

    @Test
    @DisplayName("Context loads")
    void contextLoads() {}
}