ALTER TABLE outbound_items ADD COLUMN storage_condition VARCHAR(255);

UPDATE outbound_items AS item
JOIN products AS product ON item.product_id = product.product_id
SET item.storage_condition = product.storage_condition;

ALTER TABLE outbound_items ADD CONSTRAINT fk_outbound_storage_profile
    FOREIGN KEY (storage_condition) REFERENCES storage_profiles(code);
