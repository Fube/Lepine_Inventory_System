package com.lepine.transfers.data.role;

import com.lepine.transfers.data.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
