package com.lepine.transfers.data.confirmation;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Table(schema = "lepine", name = "confirmations")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Confirmation {

    @Builder.Default
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @Id
    private UUID uuid = UUID.randomUUID();

    private int quantity;

    @JoinColumn(name = "transfer_uuid", referencedColumnName = "uuid", nullable = false)
    private UUID transferUuid;
}
