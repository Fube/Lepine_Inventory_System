package com.lepine.transfers.services;

import com.lepine.transfers.services.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { UserServiceImpl.class })
@ActiveProfiles("test")
public class UserServiceTests {

    @Test
    void contextLoads() {}
}
