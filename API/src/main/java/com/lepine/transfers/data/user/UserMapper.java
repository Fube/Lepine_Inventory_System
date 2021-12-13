package com.lepine.transfers.data.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
        @Mapping(target = "uuid", ignore = true),
    })
    User toEntity(UserUUIDLessDTO userDTO);

    @Mappings({
        @Mapping(target = "password", ignore = true),
    })
    User toEntity(UserPasswordLessDTO userDTO);
    
    UserPasswordLessDTO toPasswordLessDTO(User user);
}
