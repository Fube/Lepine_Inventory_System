package com.lepine.transfers.data.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransferRepo extends JpaRepository<Transfer, UUID> {
}
