package com.lepine.transfers.data.user;

import com.lepine.transfers.data.auth.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
        @Mapping(target = "uuid", ignore = true),
    })
    User toEntity(UserUUIDLessDTO userDTO);
    
    UserPasswordLessDTO toPasswordLessDTO(User user);

    default String map(Role value) {
        if(value.getName().startsWith("ROLE_")) {
            return value.getName().substring(5);
        }
        return value.getName();
    }

    default Role map(String value) {
        if(value.startsWith("ROLE_")) {
            value = value.substring(5);
        }
        return Role.builder()
            .name(value)
            .uuid(null)
            .build();
    }
}
