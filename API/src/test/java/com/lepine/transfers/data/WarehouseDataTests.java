package com.lepine.transfers.data;

import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WarehouseDataTests {

    @Autowired
    private WarehouseRepo warehouseRepo;

    @BeforeEach
    void setup() {
        warehouseRepo.deleteAll();
    }

    @Test
    void contextLoads(){}
}
