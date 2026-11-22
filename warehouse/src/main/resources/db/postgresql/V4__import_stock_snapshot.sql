-- Initial stock snapshot from warehouse_db_postgresql.sql.
INSERT INTO storage_profiles (code, avoid_light, fire_protection, incompatible_groups)
VALUES ('AMBIENT', FALSE, FALSE, ''), ('COLD', FALSE, FALSE, ''), ('FROZEN', FALSE, FALSE, '');

INSERT INTO products (product_id, sku_code, name, category, unit, has_expiry, storage_condition, min_stock, expiry_alert_days, is_active, perishable) VALUES
(1, 'MI-HH-001', 'Mì ăn liền Hảo Hảo (thùng 30 gói)', 'Thực phẩm khô', 'Thùng', TRUE, 'AMBIENT', 50, 60, TRUE, FALSE),
(2, 'DAU-NA-001', 'Dầu ăn Neptune 1L', 'Dầu ăn', 'Chai', TRUE, 'AMBIENT', 100, 90, TRUE, FALSE),
(3, 'SUA-VM-001', 'Sữa tươi tiệt trùng 1L', 'Sữa', 'Hộp', TRUE, 'COLD', 200, 10, TRUE, TRUE),
(4, 'SCH-VM-001', 'Sữa chua ăn (lốc 4 hộp)', 'Sữa', 'Lốc', TRUE, 'COLD', 100, 5, TRUE, TRUE),
(5, 'BO-DL-001', 'Thịt bò đông lạnh nhập khẩu', 'Thịt đông lạnh', 'Kg', TRUE, 'FROZEN', 80, 30, TRUE, TRUE),
(6, 'CV-DL-001', 'Cá viên đông lạnh 500g', 'Thực phẩm đông lạnh', 'Gói', TRUE, 'FROZEN', 100, 30, TRUE, TRUE),
(7, 'NK-LF-001', 'Nước khoáng 500ml (thùng 24 chai)', 'Đồ uống', 'Thùng', TRUE, 'AMBIENT', 100, 60, TRUE, FALSE),
(8, 'BQ-CS-001', 'Bánh quy bơ hộp 400g', 'Bánh kẹo', 'Hộp', TRUE, 'AMBIENT', 60, 30, TRUE, FALSE),
(9, 'GAO-ST-001', 'Gạo ST25 túi 5kg', 'Lương thực', 'Túi', TRUE, 'AMBIENT', 150, 120, TRUE, FALSE),
(10, 'KG-PP-001', 'Khăn giấy rút (thùng 48 gói)', 'Gia dụng', 'Thùng', FALSE, 'AMBIENT', 40, 0, TRUE, FALSE),
(11, 'NR-SL-001', 'Nước rửa chén 750ml', 'Hóa phẩm', 'Chai', TRUE, 'AMBIENT', 80, 90, TRUE, FALSE),
(12, 'DG-BH-001', 'Đường tinh luyện 1kg', 'Thực phẩm khô', 'Gói', TRUE, 'AMBIENT', 120, 120, TRUE, FALSE),
(13, 'CF-G7-001', 'Cà phê hòa tan 3in1 (hộp 18 gói)', 'Đồ uống', 'Hộp', TRUE, 'AMBIENT', 60, 90, TRUE, FALSE),
(14, 'YK-YL-001', 'Sữa chua uống (lốc 5 chai)', 'Sữa', 'Lốc', TRUE, 'COLD', 100, 5, TRUE, TRUE),
(15, 'TOM-DL-001', 'Tôm sú đông lạnh', 'Hải sản đông lạnh', 'Kg', TRUE, 'FROZEN', 60, 30, TRUE, TRUE);
SELECT setval(pg_get_serial_sequence('products', 'product_id'), (SELECT max(product_id) FROM products));
INSERT INTO suppliers (supplier_id, name, phone, address, license_no, is_active, supplier_code) VALUES
(1, 'Công ty TNHH Thực phẩm Bình Minh', '0243000001', '10 Khu công nghiệp số 1, Việt Nam', 'GP-0001/2024', TRUE, 'NCC-01'),
(2, 'Công ty CP Sữa Xanh Việt', '0243000002', '11 Khu công nghiệp số 2, Việt Nam', 'GP-0002/2024', TRUE, 'NCC-02'),
(3, 'Công ty TNHH Nước giải khát Thanh Tâm', '0243000003', '12 Khu công nghiệp số 3, Việt Nam', 'GP-0003/2024', TRUE, 'NCC-03'),
(4, 'Công ty CP Thực phẩm Đông lạnh Hải Long', '0243000004', '13 Khu công nghiệp số 4, Việt Nam', 'GP-0004/2024', TRUE, 'NCC-04'),
(5, 'Công ty TNHH Bánh kẹo Hương Quê', '0243000005', '14 Khu công nghiệp số 5, Việt Nam', 'GP-0005/2024', TRUE, 'NCC-05'),
(6, 'Công ty CP Lương thực Đồng Bằng', '0243000006', '15 Khu công nghiệp số 6, Việt Nam', 'GP-0006/2024', TRUE, 'NCC-06'),
(7, 'Công ty TNHH Giấy Sài Gòn Xanh', '0243000007', '16 Khu công nghiệp số 7, Việt Nam', 'GP-0007/2024', TRUE, 'NCC-07'),
(8, 'Công ty TNHH Hóa mỹ phẩm Gia Phú', '0243000008', '17 Khu công nghiệp số 8, Việt Nam', 'GP-0008/2024', TRUE, 'NCC-08'),
(9, 'Công ty CP Đường Mía Phú Thọ', '0243000009', '18 Khu công nghiệp số 9, Việt Nam', 'GP-0009/2024', TRUE, 'NCC-09'),
(10, 'Công ty TNHH Cà phê Cao Nguyên', '0243000010', '19 Khu công nghiệp số 10, Việt Nam', 'GP-0010/2024', FALSE, 'NCC-10');
SELECT setval(pg_get_serial_sequence('suppliers', 'supplier_id'), (SELECT max(supplier_id) FROM suppliers));
INSERT INTO locations (location_id, code, warehouse_name, zone_name, storage_condition, is_locked, is_active, is_quarantine) VALUES
(1, 'A-01-01', 'Kho trung tâm', 'Khu A - Hàng khô', 'AMBIENT', FALSE, TRUE, FALSE),
(2, 'A-01-02', 'Kho trung tâm', 'Khu A - Hàng khô', 'AMBIENT', FALSE, TRUE, FALSE),
(3, 'A-01-03', 'Kho trung tâm', 'Khu A - Hàng khô', 'AMBIENT', FALSE, TRUE, FALSE),
(4, 'A-01-04', 'Kho trung tâm', 'Khu A - Hàng khô', 'AMBIENT', FALSE, TRUE, FALSE),
(5, 'A-02-01', 'Kho trung tâm', 'Khu A - Hàng khô', 'AMBIENT', FALSE, TRUE, FALSE),
(6, 'A-02-02', 'Kho trung tâm', 'Khu A - Hàng khô', 'AMBIENT', TRUE, TRUE, FALSE),
(7, 'B-01-01', 'Kho trung tâm', 'Khu B - Kho lạnh', 'COLD', FALSE, TRUE, FALSE),
(8, 'B-01-02', 'Kho trung tâm', 'Khu B - Kho lạnh', 'COLD', FALSE, TRUE, FALSE),
(9, 'B-01-03', 'Kho trung tâm', 'Khu B - Kho lạnh', 'COLD', FALSE, TRUE, FALSE),
(10, 'C-01-01', 'Kho trung tâm', 'Khu C - Kho đông', 'FROZEN', FALSE, TRUE, FALSE),
(11, 'C-01-02', 'Kho trung tâm', 'Khu C - Kho đông', 'FROZEN', FALSE, TRUE, FALSE),
(12, 'B-02-01', 'Kho trung tâm', 'Khu B - Kho lạnh', 'COLD', FALSE, TRUE, FALSE);
SELECT setval(pg_get_serial_sequence('locations', 'location_id'), (SELECT max(location_id) FROM locations));
INSERT INTO batches (batch_id, product_id, supplier_id, batch_no, mfg_date, expiry_date, received_at, status) VALUES
(1, 1, 1, 'MI2509-A', '2026-08-20', '2027-04-30', '2026-09-10 10:00:00', 'AVAILABLE'),
(2, 2, 1, 'DAU2508-B', '2026-08-15', '2027-08-15', '2026-09-10 10:30:00', 'AVAILABLE'),
(3, 3, 2, 'SUA2611-A', '2026-11-01', '2026-11-28', '2026-11-03 09:00:00', 'AVAILABLE'),
(4, 3, 2, 'SUA2611-B', '2026-11-06', '2026-12-20', '2026-11-08 09:00:00', 'AVAILABLE'),
(5, 4, 2, 'SCH2610-A', '2026-10-25', '2026-11-10', '2026-10-30 09:30:00', 'EXPIRED'),
(6, 5, 4, 'BO2509-A', '2026-09-01', '2027-02-01', '2026-10-05 14:00:00', 'AVAILABLE'),
(7, 6, 4, 'CV2509-A', '2026-09-20', '2027-01-15', '2026-10-12 14:00:00', 'QUARANTINE'),
(8, 7, 3, 'NK2509-A', '2026-09-10', '2027-10-01', '2026-09-25 11:00:00', 'AVAILABLE'),
(9, 8, 5, 'BQ2509-A', '2026-09-15', '2026-12-05', '2026-10-20 15:00:00', 'DAMAGED'),
(10, 10, 7, 'KG2509-A', NULL, NULL, '2026-09-28 16:00:00', 'AVAILABLE');
SELECT setval(pg_get_serial_sequence('batches', 'batch_id'), (SELECT max(batch_id) FROM batches));
INSERT INTO inventory (inventory_id, batch_id, location_id, qty_on_hand, qty_reserved, updated_at) VALUES
(1, 1, 1, 500, 0, '2026-11-13 18:00:00'),
(2, 2, 2, 300, 0, '2026-11-13 18:00:00'),
(3, 3, 7, 280, 0, '2026-11-13 18:00:00'),
(4, 4, 8, 400, 0, '2026-11-13 18:00:00'),
(5, 4, 12, 100, 0, '2026-11-13 18:00:00'),
(6, 5, 9, 150, 0, '2026-11-13 18:00:00'),
(7, 6, 10, 200, 0, '2026-11-13 18:00:00'),
(8, 7, 11, 120, 0, '2026-11-13 18:00:00'),
(9, 8, 3, 414, 0, '2026-11-13 18:00:00'),
(10, 9, 4, 180, 0, '2026-11-13 18:00:00'),
(11, 10, 5, 250, 0, '2026-11-13 18:00:00');
SELECT setval(pg_get_serial_sequence('inventory', 'inventory_id'), (SELECT max(inventory_id) FROM inventory));

INSERT INTO users (username, password_hash, full_name, role, is_active, created_at)
VALUES ('stock-import', 'disabled', 'Initial stock import', 'STAFF', FALSE, LOCALTIMESTAMP);

INSERT INTO inventory_movements (batch_id, to_location_id, movement_type, reference_no,
                                change_qty, created_by, created_at, reason)
SELECT i.batch_id, i.location_id, 'ADJUSTMENT', 'OPENING-' || i.inventory_id,
       i.qty_on_hand, u.user_id, i.updated_at, 'Imported opening stock'
FROM inventory i CROSS JOIN users u
WHERE u.username = 'stock-import' AND i.qty_on_hand > 0;
