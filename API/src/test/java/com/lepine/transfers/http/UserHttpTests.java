package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.services.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = { UserController.class })
@ContextConfiguration(classes = { MapperConfig.class })
@ActiveProfiles("test")
public class UserHttpTests {
    
    @Autowired
    private MockMvc mvc;

    @SpyBean
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper itemMapper;

    @MockBean
    private UserService userService;

    @Test
    void contextLoads(){}
}
