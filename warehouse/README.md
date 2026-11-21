# Hệ thống quản lý kho tập trung

Bộ khung Java 17, Spring Boot 3.5.16, Maven và MySQL 8.x. Mở `/` để xem trang đăng nhập hoặc `/pages/dashboard.html` để duyệt các trang. Giao diện hiện ở mức khung HTML/CSS/JS.

## Chạy với MySQL đang có

Cần Bash (WSL Ubuntu dùng được), JDK 17, Maven 3.6.3 trở lên và MySQL 8.x. Tài khoản ứng dụng phải kết nối được đến một database đã tạo, có quyền tạo bảng lịch sử Flyway. Ví dụ dưới đây dùng database và user cùng tên `warehouse`.

```bash
bash setup.sh
cd warehouse
export DB_HOST=127.0.0.1 DB_PORT=3306 DB_NAME=warehouse DB_USER=warehouse
read -rsp 'Mật khẩu MySQL của warehouse: ' DB_PASSWORD && echo
export DB_PASSWORD
export JWT_SECRET="$(openssl rand -base64 32)"
export JWT_EXPIRATION_SECONDS=3600
mvn spring-boot:run
```

Giữ nguyên khóa trong cùng phiên chạy. Nếu đã có bí mật Base64 của môi trường thì dùng giá trị đó. Có thể đặt `DB_URL` để thay toàn bộ JDBC URL, gồm cả tham số TLS của máy chủ.

| Chế độ | Cách khởi động | Flyway | Hibernate |
|---|---|---|---|
| Chạy thử skeleton | `mvn spring-boot:run` | Bật, target 0, chưa áp dụng V1/V2 | Profile dev dùng `none` |
| Kiểm tra hợp đồng CSDL | `mvn spring-boot:run -Dspring-boot.run.profiles=default` | Bật, áp dụng đến bản mới nhất | `validate` |
| Chạy JAR | `mvn package` rồi `java -jar target/warehouse-0.0.1-SNAPSHOT.jar` | Bật, áp dụng đến bản mới nhất | `validate` khi không đặt profile dev |

Hai migration có kích thước 0 byte. Khi nhóm đã chốt SQL, dán DDL vào `V1__schema.sql`, dữ liệu ban đầu vào `V2__seed.sql`, rồi chạy chế độ kiểm tra hợp đồng CSDL trên database mới hoặc database đã chạy profile dev này. Điền cả hai trước lần Flyway áp dụng V1/V2, vì Flyway lưu checksum khi chạy. Nếu dự án đã có lịch sử Flyway riêng, cần đối chiếu phiên bản migration với lịch sử đó.

## Chạy cả ứng dụng và MySQL bằng Docker Compose

Từ thư mục `warehouse/` với Docker Compose v2, đặt mật khẩu MySQL và khóa JWT. Các lệnh sau dành cho lần tạo volume phát triển mới.

```bash
export DB_PASSWORD="$(openssl rand -hex 24)"
export MYSQL_ROOT_PASSWORD="$(openssl rand -hex 24)"
export JWT_SECRET="$(openssl rand -base64 32)"
docker compose up --build
```

Lưu lại các mật khẩu để sử dụng với volume ở lần chạy sau. Compose dùng profile dev mặc định. Sau khi đã điền SQL, chạy `SPRING_PROFILES_ACTIVE=default docker compose up --build`. Cổng mặc định trên máy là 8080 cho ứng dụng, 3306 cho MySQL. Có thể đổi cổng MySQL xuất ra máy bằng `DB_PORT`.

## Thử HTTP

Ở terminal thứ hai:

```bash
curl -i http://localhost:8080/api/v1/health
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"demo"}'
curl -i http://localhost:8080/api/v1/inventory
```

| Request | HTTP dự kiến | Nội dung |
|---|---|---|
| Health | 200 | `{"status":"UP"}`. Kiểm tra HTTP, không truy vấn tồn kho |
| Login với DTO hợp lệ | 501 | `{"error":{"code":"NOT_IMPLEMENTED","message":"Chưa cài đặt","fields":{}}}` |
| GET inventory thiếu token | 401 | `UNAUTHORIZED` |
| GET inventory với JWT của CUSTOMER | 403 | `FORBIDDEN` |
| GET inventory với JWT của STAFF hoặc MANAGER | 501 | Request đã đến service |
| Login với username trống | 400 | `VALIDATION_ERROR`, có tên trường trong `fields` |

Hạ tầng xác thực nhận JWT HS256 với khóa Base64 (ít nhất 32 byte sau giải mã), `iss=warehouse`, `sub` là username, `role` là tên một vai trò, `iat` và `exp` là Unix timestamp tính bằng giây. Thời hạn tối đa lấy từ `JWT_EXPIRATION_SECONDS`. API đăng nhập đang là stub, chưa phát token. BCrypt có sẵn dưới dạng bean `PasswordEncoder` cho bước triển khai tài khoản.

## Ranh giới các tầng

- Controller nhận DTO và chuyển lời gọi đến service. `AuthController` chứa thêm health để giữ đúng danh sách file. Tất cả endpoint có `@PreAuthorize`.
- Mỗi service có interface và một implementation `@Transactional`. Các thao tác đọc dùng `readOnly=true`. Tất cả phương thức nghiệp vụ ném `UnsupportedOperationException("Chưa cài đặt")`.
- Khi triển khai `confirmInbound`, việc tạo lô, tăng tồn, ghi sổ và xác nhận phiếu cùng thuộc transaction ở service. Khi triển khai `approveOrder`, gọi `InventoryRepository.findByIdForUpdate` trong transaction giữ chỗ.
- `InventoryMovement` dùng field access, `@Immutable` và chỉ có getter. Repository chỉ công bố `findById` và câu lệnh INSERT `append`.
- Service trả DTO. Mapper là interface chờ triển khai. Luồng của CUSTOMER và DRIVER nhận cả `Principal` để service có thể giới hạn đơn hoặc chuyến của người gọi khi triển khai. DTO đơn xuất dành cho khách không chứa vị trí kho nội bộ.
- `ALREADY_CONFIRMED` và `INSUFFICIENT_STOCK` ánh xạ HTTP 409 ở exception handler. Service hiện chỉ trả 501 nên chưa phát sinh hai xung đột này.

## Hợp đồng dữ liệu cần đối chiếu với V1 của nhóm

Căn cứ: phần Database, ma trận quyền và QT01–QT36 trong [Công nghệ phần mềm](https://docs.google.com/document/d/1ZjYi64xDBpr3Tz__27Z6h6Rwr2DeixFsJZqhsbfIHgQ). Tài liệu liệt kê cột cho 12 bảng, chưa cung cấp DDL MySQL đầy đủ hoặc danh sách cột chi tiết của bảng kiểm kê. Bảng dưới ghi **chính xác các cột mà những entity hiện tại ánh xạ**.

| Bảng | Cột |
|---|---|
| `users` | `user_id`, `username`, `password_hash`, `full_name`, `email`, `phone`, `role`, `is_active`, `created_at`, `addr` |
| `suppliers` | `supplier_id`, `name`, `phone`, `address`, `license_no`, `is_active` |
| `products` | `product_id`, `sku_code`, `name`, `category`, `unit`, `has_expiry`, `expiry_alert_days`, `is_active`, `storage_condition`, `min_stock` |
| `locations` | `location_id`, `code`, `warehouse_name`, `zone_name`, `storage_condition`, `is_locked`, `is_active` |
| `batches` | `batch_id`, `product_id`, `supplier_id`, `batch_no`, `mfg_date`, `expiry_date`, `received_at`, `status` |
| `inventory` | `inventory_id`, `batch_id`, `location_id`, `qty_on_hand`, `qty_reserved`, `updated_at` |
| `inbound_orders` | `inbound_order_id`, `supplier_id`, `order_no`, `status`, `expected_at`, `created_by`, `created_at` |
| `inbound_items` | `inbound_item_id`, `inbound_order_id`, `product_id`, `batch_id`, `location_id`, `qty_ordered`, `qty_received`, `unit_cost`, `item_condition` |
| `outbound_orders` | `outbound_order_id`, `customer_id`, `order_no`, `priority`, `delivery_deadline`, `status`, `shipping_address`, `created_by`, `created_at` |
| `outbound_items` | `outbound_item_id`, `outbound_order_id`, `product_id`, `inventory_id`, `qty_requested`, `qty_picked`, `unit_price` |
| `shipments` | `shipment_id`, `carrier_name`, `outbound_order_id`, `driver_name`, `driver_phone`, `driver_id`, `tracking_no`, `status`, `dispatched_at`, `delivered_at` |
| `stocktakes` | `stocktake_id`, `location_id`, `batch_id`, `expected_qty`, `counted_qty`, `reason`, `status`, `created_by`, `created_at`, `approved_by`, `approved_at` |
| `inventory_movements` | `movement_id`, `batch_id`, `from_location_id`, `to_location_id`, `movement_type`, `reference_no`, `change_qty`, `created_by`, `created_at` |

- Tên `inbound_orders`, `license_no`, `carrier_name` và cách viết snake_case là các tên vật lý đề xuất cho nhãn chưa chuẩn hóa trong tài liệu.
- Các cột đề xuất theo quy tắc nghiệp vụ gồm `products.storage_condition`, `products.min_stock`, `inbound_items.item_condition`, `outbound_items.product_id` (xác định SKU trước khi chọn tồn) và `shipments.driver_id` (giới hạn chuyến theo tài xế). Toàn bộ cột của `stocktakes` là đề xuất; mỗi bản ghi là một lần đếm một lô tại một ô.
- Kiểu dữ liệu, nullability, identity, unique và tập giá trị enum cần khớp DDL thực tế. Khung dùng `Long` cho khóa tự tăng, `BigDecimal(18,3)` cho lượng và tiền, `LocalDate` cho ngày, `LocalDateTime` cho thời điểm. Enum lưu bằng tên (`EnumType.STRING`).
- Khung khai báo đủ endpoint nhập/xuất yêu cầu. Các URL cho phân hệ còn lại và DTO là đề xuất dựa theo chức năng trong tài liệu. MANAGER có mọi thao tác của STAFF, ADMIN phụ trách tài khoản theo ma trận của nhóm.

## Danh sách API

| HTTP | Đường dẫn | Quyền | Phương thức |
|---|---|---|---|
| GET | `/api/v1/health` | Công khai | `health` |
| POST | `/api/v1/auth/login` | Công khai | `login` |
| GET | `/api/v1/users` | ADMIN | `listUsers` |
| GET | `/api/v1/users/{id}` | ADMIN | `getUser` |
| POST | `/api/v1/users` | ADMIN | `createUser` |
| PUT | `/api/v1/users/{id}` | ADMIN | `updateUser` |
| GET | `/api/v1/products` | STAFF, MANAGER | `listProducts` |
| GET | `/api/v1/products/{id}` | STAFF, MANAGER | `getProduct` |
| POST | `/api/v1/products` | MANAGER | `createProduct` |
| PUT | `/api/v1/products/{id}` | MANAGER | `updateProduct` |
| GET | `/api/v1/suppliers` | STAFF, MANAGER | `listSuppliers` |
| GET | `/api/v1/suppliers/{id}` | STAFF, MANAGER | `getSupplier` |
| POST | `/api/v1/suppliers` | MANAGER | `createSupplier` |
| PUT | `/api/v1/suppliers/{id}` | MANAGER | `updateSupplier` |
| GET | `/api/v1/locations` | STAFF, MANAGER | `listLocations` |
| GET | `/api/v1/locations/{id}` | STAFF, MANAGER | `getLocation` |
| POST | `/api/v1/locations` | MANAGER | `createLocation` |
| PUT | `/api/v1/locations/{id}` | MANAGER | `updateLocation` |
| GET | `/api/v1/batches` | STAFF, MANAGER | `listBatches` |
| GET | `/api/v1/batches/{id}` | STAFF, MANAGER | `getBatch` |
| GET | `/api/v1/batches/{id}/trace` | MANAGER | `traceBatch` |
| GET | `/api/v1/inventory` | STAFF, MANAGER | `listInventory` |
| GET | `/api/v1/inventory/{id}` | STAFF, MANAGER | `getInventory` |
| GET | `/api/v1/inbound-orders` | STAFF, MANAGER | `listInboundOrders` |
| POST | `/api/v1/inbound-orders` | STAFF, MANAGER | `createInbound` |
| GET | `/api/v1/inbound-orders/{id}` | STAFF, MANAGER | `getInbound` |
| PUT | `/api/v1/inbound-orders/{id}` | STAFF, MANAGER | `updateInbound` |
| POST | `/api/v1/inbound-orders/{id}/receive` | STAFF, MANAGER | `receiveInbound` |
| POST | `/api/v1/inbound-orders/{id}/confirm` | MANAGER | `confirmInbound` |
| GET | `/api/v1/outbound-orders` | CUSTOMER (đơn của mình), STAFF, MANAGER | `listOrders` |
| POST | `/api/v1/outbound-orders` | CUSTOMER | `createOrder` |
| GET | `/api/v1/outbound-orders/{id}` | CUSTOMER (đơn của mình), STAFF, MANAGER | `getOrder` |
| POST | `/api/v1/outbound-orders/{id}/approve` | MANAGER | `approveOrder` |
| POST | `/api/v1/outbound-orders/{id}/pick` | STAFF, MANAGER | `pickOrder` |
| POST | `/api/v1/outbound-orders/{id}/pack` | STAFF, MANAGER | `packOrder` |
| POST | `/api/v1/outbound-orders/{id}/handover` | STAFF, MANAGER | `handoverOrder` |
| POST | `/api/v1/outbound-orders/{id}/cancel` | MANAGER | `cancelOrder` |
| GET | `/api/v1/shipments/mine` | DRIVER (chuyến của mình) | `myShipments` |
| GET | `/api/v1/shipments/{id}` | DRIVER (chuyến của mình) | `getShipment` |
| POST | `/api/v1/shipments/{id}/accept` | DRIVER (chuyến của mình) | `acceptShipment` |
| GET | `/api/v1/stocktakes` | STAFF, MANAGER | `listStocktakes` |
| POST | `/api/v1/stocktakes` | STAFF, MANAGER | `createStocktake` |
| GET | `/api/v1/stocktakes/{id}` | STAFF, MANAGER | `getStocktake` |
| POST | `/api/v1/stocktakes/{id}/count` | STAFF, MANAGER | `recordCount` |
| POST | `/api/v1/stocktakes/{id}/approve` | MANAGER | `approveAdjustment` |
| GET | `/api/v1/movements` | MANAGER | `listMovements` |
| POST | `/api/v1/movements/transfer` | STAFF, MANAGER | `transferStock` |
| GET | `/api/v1/reports/stock-summary` | MANAGER | `stockSummary` |

## Cây thư mục

```text
warehouse/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── warehouse/
│   │   │           ├── config/
│   │   │           │   ├── JwtConfig.java
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   └── WebConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── BatchController.java
│   │   │           │   ├── InboundOrderController.java
│   │   │           │   ├── InventoryController.java
│   │   │           │   ├── LocationController.java
│   │   │           │   ├── MovementController.java
│   │   │           │   ├── OutboundOrderController.java
│   │   │           │   ├── ProductController.java
│   │   │           │   ├── ReportController.java
│   │   │           │   ├── ShipmentController.java
│   │   │           │   ├── StocktakeController.java
│   │   │           │   ├── SupplierController.java
│   │   │           │   └── UserController.java
│   │   │           ├── dto/
│   │   │           │   ├── request/
│   │   │           │   │   ├── AuthRequest.java
│   │   │           │   │   ├── BatchRequest.java
│   │   │           │   │   ├── InboundOrderRequest.java
│   │   │           │   │   ├── InventoryRequest.java
│   │   │           │   │   ├── LocationRequest.java
│   │   │           │   │   ├── MovementRequest.java
│   │   │           │   │   ├── OutboundOrderRequest.java
│   │   │           │   │   ├── ProductRequest.java
│   │   │           │   │   ├── ReportRequest.java
│   │   │           │   │   ├── ShipmentRequest.java
│   │   │           │   │   ├── StocktakeRequest.java
│   │   │           │   │   ├── SupplierRequest.java
│   │   │           │   │   └── UserRequest.java
│   │   │           │   └── response/
│   │   │           │       ├── AuthResponse.java
│   │   │           │       ├── BatchResponse.java
│   │   │           │       ├── InboundOrderResponse.java
│   │   │           │       ├── InventoryResponse.java
│   │   │           │       ├── LocationResponse.java
│   │   │           │       ├── MovementResponse.java
│   │   │           │       ├── OutboundOrderResponse.java
│   │   │           │       ├── ProductResponse.java
│   │   │           │       ├── ReportResponse.java
│   │   │           │       ├── ShipmentResponse.java
│   │   │           │       ├── StocktakeResponse.java
│   │   │           │       ├── SupplierResponse.java
│   │   │           │       └── UserResponse.java
│   │   │           ├── entity/
│   │   │           │   ├── enums/
│   │   │           │   │   ├── BatchStatus.java
│   │   │           │   │   ├── InboundStatus.java
│   │   │           │   │   ├── ItemCondition.java
│   │   │           │   │   ├── MovementType.java
│   │   │           │   │   ├── OutboundStatus.java
│   │   │           │   │   ├── Priority.java
│   │   │           │   │   ├── Role.java
│   │   │           │   │   ├── ShipmentStatus.java
│   │   │           │   │   └── StocktakeStatus.java
│   │   │           │   ├── Batch.java
│   │   │           │   ├── InboundItem.java
│   │   │           │   ├── InboundOrder.java
│   │   │           │   ├── Inventory.java
│   │   │           │   ├── InventoryMovement.java
│   │   │           │   ├── Location.java
│   │   │           │   ├── OutboundItem.java
│   │   │           │   ├── OutboundOrder.java
│   │   │           │   ├── Product.java
│   │   │           │   ├── Shipment.java
│   │   │           │   ├── Stocktake.java
│   │   │           │   ├── Supplier.java
│   │   │           │   └── User.java
│   │   │           ├── exception/
│   │   │           │   ├── AppException.java
│   │   │           │   ├── ErrorCode.java
│   │   │           │   └── GlobalExceptionHandler.java
│   │   │           ├── mapper/
│   │   │           │   ├── BatchMapper.java
│   │   │           │   ├── InboundOrderMapper.java
│   │   │           │   ├── InventoryMapper.java
│   │   │           │   ├── LocationMapper.java
│   │   │           │   ├── MovementMapper.java
│   │   │           │   ├── OutboundOrderMapper.java
│   │   │           │   ├── ProductMapper.java
│   │   │           │   ├── ShipmentMapper.java
│   │   │           │   ├── StocktakeMapper.java
│   │   │           │   ├── SupplierMapper.java
│   │   │           │   └── UserMapper.java
│   │   │           ├── repository/
│   │   │           │   ├── BatchRepository.java
│   │   │           │   ├── InboundItemRepository.java
│   │   │           │   ├── InboundOrderRepository.java
│   │   │           │   ├── InventoryMovementRepository.java
│   │   │           │   ├── InventoryRepository.java
│   │   │           │   ├── LocationRepository.java
│   │   │           │   ├── OutboundItemRepository.java
│   │   │           │   ├── OutboundOrderRepository.java
│   │   │           │   ├── ProductRepository.java
│   │   │           │   ├── ShipmentRepository.java
│   │   │           │   ├── StocktakeRepository.java
│   │   │           │   ├── SupplierRepository.java
│   │   │           │   └── UserRepository.java
│   │   │           ├── security/
│   │   │           │   ├── CustomUserDetails.java
│   │   │           │   ├── JwtAuthFilter.java
│   │   │           │   └── JwtService.java
│   │   │           ├── service/
│   │   │           │   ├── impl/
│   │   │           │   │   ├── AuthServiceImpl.java
│   │   │           │   │   ├── BatchServiceImpl.java
│   │   │           │   │   ├── InboundOrderServiceImpl.java
│   │   │           │   │   ├── InventoryServiceImpl.java
│   │   │           │   │   ├── LocationServiceImpl.java
│   │   │           │   │   ├── MovementServiceImpl.java
│   │   │           │   │   ├── OutboundOrderServiceImpl.java
│   │   │           │   │   ├── ProductServiceImpl.java
│   │   │           │   │   ├── ReportServiceImpl.java
│   │   │           │   │   ├── ShipmentServiceImpl.java
│   │   │           │   │   ├── StocktakeServiceImpl.java
│   │   │           │   │   ├── SupplierServiceImpl.java
│   │   │           │   │   └── UserServiceImpl.java
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── BatchService.java
│   │   │           │   ├── InboundOrderService.java
│   │   │           │   ├── InventoryService.java
│   │   │           │   ├── LocationService.java
│   │   │           │   ├── MovementService.java
│   │   │           │   ├── OutboundOrderService.java
│   │   │           │   ├── ProductService.java
│   │   │           │   ├── ReportService.java
│   │   │           │   ├── ShipmentService.java
│   │   │           │   ├── StocktakeService.java
│   │   │           │   ├── SupplierService.java
│   │   │           │   └── UserService.java
│   │   │           ├── util/
│   │   │           │   ├── BatchNoGenerator.java
│   │   │           │   └── PageUtil.java
│   │   │           └── WarehouseApplication.java
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__schema.sql
│   │       │       └── V2__seed.sql
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   ├── base.css
│   │       │   │   ├── components.css
│   │       │   │   └── layout.css
│   │       │   ├── js/
│   │       │   │   ├── components/
│   │       │   │   ├── pages/
│   │       │   │   │   ├── batch-trace.js
│   │       │   │   │   ├── dashboard.js
│   │       │   │   │   ├── inbound-detail.js
│   │       │   │   │   ├── inbound-form.js
│   │       │   │   │   ├── inbound-list.js
│   │       │   │   │   ├── inbound-receive.js
│   │       │   │   │   ├── index.js
│   │       │   │   │   ├── inventory.js
│   │       │   │   │   ├── locations.js
│   │       │   │   │   ├── my-shipments.js
│   │       │   │   │   ├── outbound-detail.js
│   │       │   │   │   ├── outbound-form.js
│   │       │   │   │   ├── outbound-list.js
│   │       │   │   │   ├── products.js
│   │       │   │   │   ├── reports.js
│   │       │   │   │   ├── stocktake.js
│   │       │   │   │   ├── suppliers.js
│   │       │   │   │   ├── transfer.js
│   │       │   │   │   └── users.js
│   │       │   │   ├── api.js
│   │       │   │   ├── auth.js
│   │       │   │   ├── config.js
│   │       │   │   └── mock.js
│   │       │   ├── pages/
│   │       │   │   ├── batch-trace.html
│   │       │   │   ├── dashboard.html
│   │       │   │   ├── inbound-detail.html
│   │       │   │   ├── inbound-form.html
│   │       │   │   ├── inbound-list.html
│   │       │   │   ├── inbound-receive.html
│   │       │   │   ├── inventory.html
│   │       │   │   ├── locations.html
│   │       │   │   ├── my-shipments.html
│   │       │   │   ├── outbound-detail.html
│   │       │   │   ├── outbound-form.html
│   │       │   │   ├── outbound-list.html
│   │       │   │   ├── products.html
│   │       │   │   ├── reports.html
│   │       │   │   ├── stocktake.html
│   │       │   │   ├── suppliers.html
│   │       │   │   ├── transfer.html
│   │       │   │   └── users.html
│   │       │   └── index.html
│   │       ├── application-dev.yml
│   │       └── application.yml
│   └── test/
│       └── java/
│           └── com/
│               └── warehouse/
│                   ├── controller/
│                   └── service/
├── .gitignore
├── Dockerfile
├── README.md
├── docker-compose.yml
└── pom.xml
```

Ba thư mục rỗng là khung cho JS dùng chung và kiểm thử. Git chỉ lưu file, nên nếu checkout một repository chỉ gồm các file đã commit, có thể tạo lại ba thư mục này bằng `mkdir -p src/main/resources/static/js/components src/test/java/com/warehouse/{service,controller}`.
