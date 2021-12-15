package com.lepine.transfers.data;

import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.role.RoleRepo;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import org.assertj.core.util.Throwables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserDataTests {
    
    private static final String VALID_EMAIL = "abc@def.com";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";
    private static final String VALID_ROLE_NAME = "SOME_ROLE";
    private static final Role VALID_ROLE = Role.builder()
            .name(VALID_ROLE_NAME)
            .build();

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given user, persist")
    void save() {

        final Role savedRole = roleRepo.save(VALID_ROLE);
        entityManager.flush();
        final User save = userRepo.save(
                User.builder()
                        .email(VALID_EMAIL)
                        .password(VALID_PASSWORD)
                        .role(savedRole)
                        .build()
        );

        assertEquals(VALID_EMAIL, save.getEmail());
        assertEquals(VALID_PASSWORD, save.getPassword());
    }

    @Test
    @DisplayName("Given user with null email, throw Exception")
    void save_NullEmail() {

        // Arrange
        final User user = User.builder()
            .email(null)
            .password(VALID_PASSWORD)
            .build();

        // Act
        userRepo.save(user);
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () -> entityManager.flush());
        // Assert
        Throwable exception = Throwables.getRootCause(persistenceException);
        assertThat(exception.getMessage()).contains("NULL not allowed");
    }

    @Test
    @DisplayName("Given user with null password, throw Exception")
    void save_NullPassword() {

        // Arrange
        final User user = User.builder()
            .email(VALID_EMAIL)
            .password(null)
            .build();

        // Act
        userRepo.save(user);
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () -> entityManager.flush());
        // Assert
        Throwable exception = Throwables.getRootCause(persistenceException);
        assertThat(exception.getMessage()).contains("NULL not allowed");
    }

    @Test
    @DisplayName("Given user with duplicate email, throw Exception")
    void save_DuplicateEmail() {

        // Arrange
        final Role savedRole = roleRepo.save(VALID_ROLE);
        entityManager.flush();

        final User user = User.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(savedRole)
            .build();

        // Act
        userRepo.save(user);
        userRepo.save(user.toBuilder().build());
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () -> entityManager.flush());
        // Assert
        Throwable exception = Throwables.getRootCause(persistenceException);
        assertThat(exception.getMessage()).contains("Unique");
    }

    @Test
    @DisplayName("Given email, retrieve user")
    void findByEmail() {

        // Arrange
        final Role savedRole = roleRepo.save(VALID_ROLE);
        entityManager.flush();

        final User user = userRepo.save(
                User.builder()
                        .email(VALID_EMAIL)
                        .password(VALID_PASSWORD)
                        .role(savedRole)
                        .build()
        );
        entityManager.flush();

        // Act
        final Optional<User> found = userRepo.findByEmail(VALID_EMAIL);

        // Assert
        assertTrue(found.isPresent());
        final User got = found.get();
        assertEquals(user.getEmail(), got.getEmail());
        assertEquals(user.getPassword(), got.getPassword());
    }

    @Test
    @DisplayName("Given user with no role, throw DataIntegrityViolationException")
    void save_NoRole() {

        // Arrange
        final User user = User.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        // Act
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () -> {
                    userRepo.save(user);
                    entityManager.flush();
                });

        // Assert
        Throwable exception = Throwables.getRootCause(persistenceException);
        assertThat(exception.getMessage()).contains("NULL not allowed");
    }
}
