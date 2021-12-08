package com.lepine.transfers.http;

import com.lepine.transfers.controllers.item.ItemController;
import com.lepine.transfers.services.item.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = { ItemController.class })
@ContextConfiguration(classes = { Config.class })
@ActiveProfiles("test")
public class ItemHttpTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @SpyBean
    private ItemController itemController;

    @Test
    void contextLoads(){}
}
