package com.lepine.transfers.services;

import com.lepine.transfers.data.transfer.TransferRepo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
})
@ActiveProfiles({"test"})
public class TransferServiceTests {

    @MockBean
    private TransferRepo transferRepo;

    @Test
    void contextLoads() {}
}
