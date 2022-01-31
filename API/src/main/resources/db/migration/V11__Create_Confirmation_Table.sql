CREATE TABLE lepine.confirmations(
     uuid uuid NOT NULL UNIQUE,
     quantity integer NOT NULL,
     transfer_uuid uuid NOT NULL REFERENCES lepine.transfers(uuid) ON DELETE CASCADE,
     PRIMARY KEY (uuid)
)