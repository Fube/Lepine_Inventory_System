package com.lepine.transfers.services.user;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.user.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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
        return null;
    }

    @Override
    public Page<User> findAll(PageRequest pageRequest) {
        return null;
    }
}
