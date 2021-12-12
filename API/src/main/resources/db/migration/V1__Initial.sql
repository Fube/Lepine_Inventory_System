CREATE SCHEMA IF NOT EXISTS lepine;

CREATE TABLE lepine.items(
    uuid uuid PRIMARY KEY,
    sku VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1023)
)