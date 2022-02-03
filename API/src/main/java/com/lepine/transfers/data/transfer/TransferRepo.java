package com.lepine.transfers.data.transfer;

import com.lepine.transfers.data.confirmation.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransferRepo extends JpaRepository<Transfer, UUID> {

    @Query(
            "select t as confirmed from Transfer t " +
                    "join Confirmation c on t.uuid = c.transferUuid " +
                        "group by t.uuid " +
                            "having sum(c.quantity) = t.quantity"
    )
    List<Transfer> findAllFullyConfirmed();
}
