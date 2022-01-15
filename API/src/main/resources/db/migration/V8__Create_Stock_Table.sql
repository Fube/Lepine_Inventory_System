CREATE TABLE lepine.stock(
    uuid uuid NOT NULL UNIQUE PRIMARY KEY,
    quantity integer NOT NULL,
    item_uuid uuid NOT NULL REFERENCES lepine.item(uuid) ON DELETE CASCADE,
    warehouse_uuid uuid NOT NULL REFERENCES lepine.warehouse(uuid) ON DELETE CASCADE,
    UNIQUE(item_uuid, warehouse_uuid)
)