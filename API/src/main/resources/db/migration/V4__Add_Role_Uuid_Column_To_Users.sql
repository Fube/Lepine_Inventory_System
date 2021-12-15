ALTER TABLE lepine.users
    ADD COLUMN role_uuid uuid NOT NULL;

ALTER TABLE lepine.users
    ADD CONSTRAINT fk_customer
        FOREIGN KEY(role_uuid) REFERENCES lepine.roles(uuid);