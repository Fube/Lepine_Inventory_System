package com.lepine.transfers.data.user;

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

    @Mappings({
            @Mapping(source = "authorities", target = "role", qualifiedByName = "getRoleFromGrants")
    })
    UserPasswordLessDTO toPasswordLessDTO(UserDetails user);

    List<UserPasswordLessDTO> toPasswordLessDTOs(List<User> users);

    @Named("getRoleFromGrants")
    default String getRoleFromGrants(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority authority : authorities) {
            final String candidate = authority.getAuthority();
            if(candidate.startsWith("ROLE_)")){
                return candidate;
            }
        }
        return null;
    }
}
