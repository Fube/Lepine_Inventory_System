CREATE TABLE lepine.transfers (
      uuid uuid NOT NULL UNIQUE,
      quantity integer NOT NULL,
      stock_uuid uuid NOT NULL REFERENCES lepine.stocks(uuid) ON DELETE CASCADE,
      shipment_uuid uuid NOT NULL REFERENCES lepine.shipments(uuid) ON DELETE CASCADE,
      to uuid NOT NULL REFERENCES lepine.warehouses(uuid),
      PRIMARY KEY (uuid)
)