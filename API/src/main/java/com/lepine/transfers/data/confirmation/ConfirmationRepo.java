package com.lepine.transfers.data.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConfirmationRepo extends JpaRepository<Confirmation, UUID> {

    @Query("select sum(c.quantity) from Confirmation c where c.transferUuid = :transferUuid")
    Integer sumQuantityByTransferUuid(final UUID transferUuid);
}
