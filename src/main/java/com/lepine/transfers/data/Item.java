package com.lepine.transfers.data;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table(schema = "lepine", name = "items")
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Item {

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Id
    private UUID uuid = UUID.randomUUID();

    private String SKU;
    private String description;
    private String name;
}
