package com.lepine.transfers.controllers.user;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.user.UserNotFoundException;
import com.lepine.transfers.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "${cors.origin}")
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public UserPasswordLessDTO create(@Valid @RequestBody UserUUIDLessDTO userUUIDLessDTO) {
        log.info("Creating user with email {}", userUUIDLessDTO.getEmail());
        User created = userService.create(userUUIDLessDTO);
        log.info("Created user with UUID {}", created.getUuid());
        return userMapper.toPasswordLessDTO(created);
    }

    @GetMapping
    public Page<UserPasswordLessDTO> getAll(
            @RequestParam(value = "page", defaultValue = "1")
            @Min(value = 1, message = "{pagination.page.min}") int page,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "{pagination.size.min}") int size
    ) {
        log.info("Getting all users");

        final Page<UserPasswordLessDTO> passwordLessDTOPage = OneIndexedPageAdapter.of(userService.findAll(PageRequest.of(page - 1, size))
                .map(userMapper::toPasswordLessDTO));

        log.info("Got all users, count {}", passwordLessDTOPage.getTotalElements());

        return passwordLessDTOPage;
    }
    @PutMapping("/{uuid}")
    public UserPasswordLessDTO update(
            @PathVariable UUID uuid,
            @RequestBody @Valid UserUUIDLessDTO userUUIDLessDTO){
        log.info("Update user");
        return userMapper.toPasswordLessDTO(userService.update(uuid, userUUIDLessDTO));
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable  UUID uuid) {
        userService.delete(uuid);
    }

    @GetMapping("/{uuid}")
    public UserPasswordLessDTO getByUuid(@PathVariable @NotNull UUID uuid) {
        log.info("retrieving user by uuid {}", uuid);

        final Optional<User> byUuid = userService.findByUuid(uuid);
        if(byUuid.isEmpty()) {
            log.info("user with uuid {} not found", uuid);
            throw new UserNotFoundException(uuid);
        }

        final User user = byUuid.get();
        log.info("retrieved user by uuid {}", user.getUuid());

        return userMapper.toPasswordLessDTO(user);
    }
}
