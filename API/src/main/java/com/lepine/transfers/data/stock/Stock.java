package com.lepine.transfers.data.stock;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.warehouse.Warehouse;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Table(schema = "lepine", name = "stock")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Stock {

    @Builder.Default
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @Id
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "item_uuid")
    private Item item;

    @ManyToOne
    @JoinColumn(name="warehouse_uuid")
    private Warehouse warehouse;

    private Integer Quantity;
}
