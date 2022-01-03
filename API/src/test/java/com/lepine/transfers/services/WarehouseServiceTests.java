package com.lepine.transfers.services;

import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.services.warehouse.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {})
@ActiveProfiles({"test"})
public class WarehouseServiceTests {

    @Autowired
    private WarehouseService warehouseService;

    @MockBean
    private WarehouseRepo warehouseRepo;

    @Test
    void contextLoads(){}
}
