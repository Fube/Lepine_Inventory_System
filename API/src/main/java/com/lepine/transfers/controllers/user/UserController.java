package com.lepine.transfers.controllers.user;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;

import com.lepine.transfers.services.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "${cors.origin}")
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;

    public UserPasswordLessDTO create(@Valid UserUUIDLessDTO userUUIDLessDTO) {
        log.info("Creating user with email {}", userUUIDLessDTO.getEmail());
        User created = userService.create(userUUIDLessDTO);
        log.info("Created user with UUID {}", created.getUuid());
        return userMapper.toPasswordLessDTO(created);
    }
    
    public Page<UserPasswordLessDTO> getAll(
            @Min(value = 1, message = "{pagination.page.min}") int page,
            @Min(value = 1, message = "{pagination.size.min}") int size
    ) {
        log.info("Getting all users");

        final Page<UserPasswordLessDTO> passwordLessDTOPage = OneIndexedPageAdapter.of(userService.findAll(PageRequest.of(page - 1, size))
                .map(userMapper::toPasswordLessDTO));

        log.info("Got all users, count {}", passwordLessDTOPage.getTotalElements());

        return passwordLessDTOPage;
    }
}
