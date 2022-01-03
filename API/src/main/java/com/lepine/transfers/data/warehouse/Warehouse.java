package com.lepine.transfers.data.warehouse;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table(schema = "lepine", name = "warehouses")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Warehouse {

    @Builder.Default
    @GeneratedValue(generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @Id
    private UUID uuid = UUID.randomUUID();

    private String zipCode;
    private String city;
    private String province;

    @Builder.Default
    private boolean isActive = true;
}
