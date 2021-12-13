package com.lepine.transfers.services.user;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserUUIDLessDTO;

public interface UserService {

    User create(UserUUIDLessDTO userUUIDLessDTO);
    
}
