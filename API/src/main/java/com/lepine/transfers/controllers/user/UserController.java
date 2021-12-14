package com.lepine.transfers.controllers.user;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;

import com.lepine.transfers.services.user.UserService;
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
    
    public List<UserPasswordLessDTO> getAll(
            @RequestParam(required = false, defaultValue = "1")
            @Min(value = 1, message = "Page number cannot be less than 1") int page,
            @RequestParam(required = false, defaultValue = "10")
            @Min(value = 1, message = "Page size cannot be less than 1") int size
    ) {
        log.info("Getting all users");
        List<UserPasswordLessDTO> passwordLessDTOS = OneIndexedPageAdapter.of(userMapper.toPasswordLessDTOs(userService.findAll(PageRequest.of(page - 1, size))));
        log.info("Got all users, count {}", passwordLessDTOS.size());

        return passwordLessDTOS;
    }
}
