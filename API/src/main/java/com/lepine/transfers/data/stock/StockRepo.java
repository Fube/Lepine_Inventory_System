package com.lepine.transfers.data.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface StockRepo extends JpaRepository<Stock, UUID> {
}
