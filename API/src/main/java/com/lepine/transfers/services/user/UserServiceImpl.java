package com.lepine.transfers.services.user;

import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.auth.InvalidLoginException;
import com.lepine.transfers.exceptions.user.DuplicateEmailException;
import com.lepine.transfers.services.auth.AuthService;
import com.lepine.transfers.utils.auth.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JWTUtil<User> jwtUtil;
    private final AuthenticationManager authenticationManager;

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
    public Pair<User, String> login(UserLogin userLogin) {

        log.info("Logging in user {}", userLogin.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword())
            );

            final Object rawPrincipal = authentication.getPrincipal();
            User principal;
            if(rawPrincipal instanceof User) {

                principal = (User) authentication.getPrincipal();
            } else {
                final UserDetails userDetails = (UserDetails) rawPrincipal;
                principal = User.builder()
                        .email(userDetails.getUsername())
                        .password(userDetails.getPassword())
                        .role(Role.builder()
                                .name(userDetails.getAuthorities().iterator().next().getAuthority())
                                .build())
                        .build();
            }

            log.info("Logged in user {}", principal.getEmail());

            return new Pair<>(principal, getJWT(principal));

        } catch (BadCredentialsException ex) {
            log.error("Password does not match");
            throw new InvalidLoginException();
        }
    }

    private String getJWT(User user) {
        log.info("Getting JWT for user {}", user.getEmail());
        final String jwt = jwtUtil.encode(user);
        log.info("Got JWT for user {}", user.getEmail());

        return jwt;
    }
}
