CREATE SCHEMA IF NOT EXISTS lepine;

/**
      private UUID uuid = UUID.randomUUID();

    private String SKU;
    private String description;
    private String name;
 */
CREATE TABLE lepine.items(
    uuid VARCHAR(36) PRIMARY KEY,
    sku VARCHAR(8),
    name VARCHAR(255),
    description VARCHAR(1023)
)