ALTER TABLE lepine.users
    ADD COLUMN role_uuid uuid
        REFERENCES lepine.roles(uuid) ON DELETE CASCADE;