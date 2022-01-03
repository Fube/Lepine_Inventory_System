CREATE TABLE lepine.warehouses (
    uuid uuid PRIMARY KEY,
    zip_code VARCHAR(255) NOT NULL UNIQUE,
    city VARCHAR(255) NOT NULL,
    province VARCHAR(64) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true
)