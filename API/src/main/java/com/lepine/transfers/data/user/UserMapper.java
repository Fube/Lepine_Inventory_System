package com.lepine.transfers.data.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
        @Mapping(target = "uuid", ignore = true),
    })
    User toEntity(UserUUIDLessDTO userDTO);
    
    UserPasswordLessDTO toPasswordLessDTO(User user);

    List<UserPasswordLessDTO> toPasswordLessDTOs(List<User> users);
}
