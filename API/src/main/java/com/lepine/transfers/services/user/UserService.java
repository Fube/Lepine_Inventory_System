package com.lepine.transfers.services.user;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UserService {

    User create(UserUUIDLessDTO userUUIDLessDTO);

    Page<User> findAll();

    Page<User> findAll(PageRequest pageRequest);
}
