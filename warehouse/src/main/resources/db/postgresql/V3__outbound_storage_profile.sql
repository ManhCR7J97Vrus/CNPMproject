ALTER TABLE outbound_items ADD COLUMN storage_condition VARCHAR(255);

UPDATE outbound_items AS item
SET storage_condition = product.storage_condition
FROM products AS product
WHERE item.product_id = product.product_id;

ALTER TABLE outbound_items ADD CONSTRAINT fk_outbound_storage_profile
    FOREIGN KEY (storage_condition) REFERENCES storage_profiles(code);
