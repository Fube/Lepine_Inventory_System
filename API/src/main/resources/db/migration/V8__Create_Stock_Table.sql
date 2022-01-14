CREATE TABLE lepine.stock (
    uuid uuid PRIMARY KEY,
    item VARCHAR(255) NOT NULL UNIQUE,
    warehouse VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
)