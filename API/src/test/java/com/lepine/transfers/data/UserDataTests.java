package com.lepine.transfers.data;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.lepine.transfers.data.user.UserRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserDataTests {
    
    private static final String VALID_EMAIL = "abc@def.com";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";

    @Autowired
    private UserRepo userRepo;

    @Test
    void contextLoads(){}
}
