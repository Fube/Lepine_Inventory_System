package com.lepine.transfers.services.user;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User create(UserUUIDLessDTO userUUIDLessDTO) {
        return null;
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
