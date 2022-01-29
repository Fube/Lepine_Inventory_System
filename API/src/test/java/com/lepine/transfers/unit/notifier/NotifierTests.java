package com.lepine.transfers.unit.notifier;

import com.lepine.transfers.notification.Notifier;
import com.lepine.transfers.services.mailer.MailerService;
import com.lepine.transfers.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes= {
        Notifier.class
})
@ActiveProfiles({"test"})
public class NotifierTests {

    @Autowired
    private Notifier notifier;

    @MockBean
    private UserService userService;

    @MockBean
    private MailerService mailerService;

    @Test
    void contextLoads(){}
}
