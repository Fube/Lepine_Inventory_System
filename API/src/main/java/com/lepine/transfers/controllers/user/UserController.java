package com.lepine.transfers.controllers.user;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

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
    @GetMapping
    public UserPasswordLessDTO update(@PathVariable UUID uuid, @Valid @RequestBody UserUUIDLessDTO userUUIDLessDTO){
        log.info("Update user with email {}", userUUIDLessDTO.getEmail());
        User updated = userService.create(userUUIDLessDTO);
        log.info("Updated user with UUID {}", updated.getUuid());
        return userMapper.toPasswordLessDTO(updated);
    }
}
