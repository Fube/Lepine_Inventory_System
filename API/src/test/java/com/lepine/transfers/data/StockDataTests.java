package com.lepine.transfers.data;

import com.lepine.transfers.data.item.ItemRepo;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockDataTests {

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @Autowired
    private StockRepo stockRepo;

    @Test
    void contextLoads(){}
}
