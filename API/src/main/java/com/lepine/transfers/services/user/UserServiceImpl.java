package com.lepine.transfers.services.user;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.user.DuplicateEmailException;
import com.lepine.transfers.exceptions.user.UserNotFoundException;
import com.lepine.transfers.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService, AuthService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(UserUUIDLessDTO userUUIDLessDTO) {
        log.info("Creating user {} with email", userUUIDLessDTO.getEmail());

        final User user = userMapper.toEntity(userUUIDLessDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(userRepo.findByEmail(user.getEmail()).isPresent()) {
            log.error("User with email {} already exists", user.getEmail());
            throw new DuplicateEmailException(user.getEmail());
        }
        final User save = userRepo.save(user);

        log.info("Created user {} with email and UUID {}", save.getEmail(), save.getUuid());

        return user;
    }

    @Override
    public Page<User> findAll() {
        return findAll(PageRequest.of(0, 10));
    }

    @Override
    public Page<User> findAll(PageRequest pageRequest) {
        log.info("Getting {} page of Users with size {}", pageRequest.getPageNumber(), pageRequest.getPageSize());
        Page<User> got = userRepo.findAll(pageRequest);
        log.info("Got {} Users in total, {} in first page", got.getTotalElements(), got.getContent().size());

        return got;
    }

    @Override
    public User login(UserLogin userLogin) {

        final String email = userLogin.getEmail();
        log.info("Logging in user {}", email);

        final User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if(!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            log.error("Password does not match");
            throw new RuntimeException("Password does not match");
        }

        log.info("Logged in user {}", email);

        return user;
    }
}
