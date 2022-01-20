package com.lepine.transfers.data;

import com.lepine.transfers.data.shipment.ShipmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShipmentDataTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ShipmentRepo shipmentRepo;
}
