package com.lepine.transfers.controllers.user;

import javax.validation.Valid;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "${cors.origin}")
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;

    public UserPasswordLessDTO create(@Valid UserPasswordLessDTO userPasswordLessDTO) {
        log.info("Creating user with email {}", userPasswordLessDTO.getEmail());
        User created = userService.create(userMapper.toEntity(userPasswordLessDTO));
        log.info("Created user with UUID {}", created.getUuid());
        return userMapper.toPasswordLessDTO(created);
    }
    

}
