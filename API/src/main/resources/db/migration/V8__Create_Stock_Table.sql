CREATE TABLE lepine.stocks(
    uuid uuid NOT NULL UNIQUE,
    quantity integer NOT NULL,
    item_uuid uuid NOT NULL REFERENCES lepine.items(uuid) ON DELETE CASCADE,
    warehouse_uuid uuid NOT NULL REFERENCES lepine.warehouses(uuid) ON DELETE CASCADE,
    UNIQUE (item_uuid, warehouse_uuid),
    PRIMARY KEY (uuid)
);