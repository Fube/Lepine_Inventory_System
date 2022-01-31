package com.lepine.transfers.data.user;

import com.lepine.transfers.data.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    
    Integer deleteByUuid(UUID uuid);
}
