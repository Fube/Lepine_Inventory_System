package com.lepine.transfers.data;

import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.role.RoleRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleDataTests {

    @Autowired
    private RoleRepo roleRepo;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given migrations, retrieve all 3 roles")
    void retrieveAllRoles(){
        // Arrange

        // Act
        final List<Role> all = roleRepo.findAll();

        // Assert
        assertEquals(3, all.size());
        assertThat(all.stream().map(Role::getName))
                .containsExactlyInAnyOrder("MANAGER", "SALESPERSON", "CLERK");
    }
}
