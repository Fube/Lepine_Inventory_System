package com.lepine.transfers.data.transfer;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.stock.Stock;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Table(schema = "lepine", name = "transfers")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transfer {

    @Builder.Default
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @Id
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_uuid")
    private Stock stock;

    private int quantity;

    @JoinColumn(name = "shipment_uuid", referencedColumnName = "uuid", nullable = false)
    @Column(name = "shipment_uuid", insertable = false, updatable = false)
    private UUID shipmentUuid;
}
