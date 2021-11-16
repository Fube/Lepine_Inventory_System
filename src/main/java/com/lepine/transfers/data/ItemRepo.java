package com.lepine.transfers.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepo extends JpaRepository<Item, UUID> {
}
