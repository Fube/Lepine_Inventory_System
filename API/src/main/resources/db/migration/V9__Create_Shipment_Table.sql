CREATE TABLE lepine.shipments (
      uuid uuid NOT NULL UNIQUE,
      status varchar(255) NOT NULL,
      expected_date timestamp with time zone NOT NULL,
      order_number varchar(255) NOT NULL,
      created_by uuid NOT NULL REFERENCES lepine.users(uuid),
      "to" uuid NOT NULL REFERENCES lepine.warehouses(uuid),
      PRIMARY KEY (uuid)
)