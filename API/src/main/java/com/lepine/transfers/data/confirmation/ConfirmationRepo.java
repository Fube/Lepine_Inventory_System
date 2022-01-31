package com.lepine.transfers.data.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConfirmationRepo extends JpaRepository<Confirmation, UUID> {
}
