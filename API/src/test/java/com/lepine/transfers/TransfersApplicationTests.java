package com.lepine.transfers;

import com.lepine.transfers.services.Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {Config.class})
@ActiveProfiles("test")
class TransfersApplicationTests {

    @Test
    void contextLoads() {
    }

}
