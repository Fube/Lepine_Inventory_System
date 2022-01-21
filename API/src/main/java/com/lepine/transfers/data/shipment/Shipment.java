package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.Transfer;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Table(schema = "lepine", name = "shipments")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Shipment {

    @Builder.Default
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @Id
    private UUID uuid = UUID.randomUUID();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipment_uuid", referencedColumnName = "uuid", nullable = false)
    private List<Transfer> transfers;

    private LocalDate expectedDate;
    private String orderNumber;

    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid", nullable = false)
    private UUID createdBy;
}
