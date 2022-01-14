package com.lepine.transfers.services.user;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface UserService {

    User create(@NotNull(message = "{user.not_null}") @Valid UserUUIDLessDTO userUUIDLessDTO);

    Page<User> findAll();

    Page<User> findAll(PageRequest pageRequest);

    User update(UUID uuid, @Valid UserUUIDLessDTO userToUpdate);
}
