ALTER TABLE lepine.stock
ADD CONSTRAINT fk_item
    FOREIGN KEY (item) REFERENCES lepine.item(sku);
ADD CONSTRAINT fk_warehouse
    FOREIGN KEY (warehouse) REFERENCES lepine.warehouse(city);