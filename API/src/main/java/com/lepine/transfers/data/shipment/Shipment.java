package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.Transfer;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
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

    private Date expectedDate;

    private String orderNumber;
}
