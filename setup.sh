#!/usr/bin/env bash
# Dựng project trong thư mục hiện tại. Chạy với --tree để xem trước.
set -euo pipefail

print_tree() {
    cat <<'WAREHOUSE_TREE'
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
WAREHOUSE_TREE
}

case "$#" in
    0) ;;
    1) if [[ "$1" == --tree ]]; then print_tree; exit 0; fi
       echo 'Cách dùng: bash setup.sh [--tree]' >&2; exit 2 ;;
    *) echo 'Cách dùng: bash setup.sh [--tree]' >&2; exit 2 ;;
esac

if [[ -e warehouse || -L warehouse ]]; then
    echo 'warehouse đã tồn tại. Hãy chọn một thư mục cha khác.' >&2
    exit 1
fi

# Cùng filesystem với đích, chỉ công bố project sau khi đã sinh đủ file.
stage=$(mktemp -d ./.warehouse-XXXXXXXX)
trap 'rm -rf -- "$stage"' EXIT
trap 'exit 130' INT
trap 'exit 143' TERM
(
    cd -- "$stage"
    mkdir -p \
        'src/main/java/com/warehouse' \
        'src/main/java/com/warehouse/config' \
        'src/main/java/com/warehouse/controller' \
        'src/main/java/com/warehouse/dto/request' \
        'src/main/java/com/warehouse/dto/response' \
        'src/main/java/com/warehouse/entity' \
        'src/main/java/com/warehouse/entity/enums' \
        'src/main/java/com/warehouse/exception' \
        'src/main/java/com/warehouse/mapper' \
        'src/main/java/com/warehouse/repository' \
        'src/main/java/com/warehouse/security' \
        'src/main/java/com/warehouse/service' \
        'src/main/java/com/warehouse/service/impl' \
        'src/main/java/com/warehouse/util' \
        'src/main/resources' \
        'src/main/resources/db/migration' \
        'src/main/resources/static' \
        'src/main/resources/static/css' \
        'src/main/resources/static/js' \
        'src/main/resources/static/js/components' \
        'src/main/resources/static/js/pages' \
        'src/main/resources/static/pages' \
        'src/test/java/com/warehouse/controller' \
        'src/test/java/com/warehouse/service'

    cat > 'src/main/java/com/warehouse/entity/enums/Role.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Vai trò dùng khi phân quyền. */
public enum Role {
    ADMIN,
    MANAGER,
    STAFF,
    CUSTOMER,
    DRIVER
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/BatchStatus.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Trạng thái sử dụng lô hàng. */
public enum BatchStatus {
    AVAILABLE,
    DAMAGED,
    QUARANTINE,
    EXPIRED
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/InboundStatus.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Trạng thái phiếu nhập kho. */
public enum InboundStatus {
    DRAFT,
    CONFIRMED
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/OutboundStatus.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Trạng thái đơn xuất kho. */
public enum OutboundStatus {
    REQUESTED,
    APPROVED,
    PICKED,
    PACKED,
    HANDED_OVER,
    CANCELLED,
    LATE,
    REFUSED
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/Priority.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Mức ưu tiên giao hàng. */
public enum Priority {
    NORMAL,
    EXPRESS,
    URGENT
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/ShipmentStatus.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Trạng thái chuyến giao hàng. */
public enum ShipmentStatus {
    ASSIGNED,
    ACCEPTED,
    DISPATCHED,
    DELIVERED,
    LATE,
    REFUSED
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/StocktakeStatus.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Trạng thái kiểm kê. */
public enum StocktakeStatus {
    DRAFT,
    COUNTED,
    APPROVED
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/MovementType.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Loại biến động được ghi sổ. */
public enum MovementType {
    INBOUND,
    OUTBOUND,
    TRANSFER,
    ADJUSTMENT
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/enums/ItemCondition.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity.enums;

/** Tình trạng hàng thực nhận. */
public enum ItemCondition {
    GOOD,
    DAMAGED,
    QUARANTINE
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/User.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng users. */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "addr")
    private String addr;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/UserRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng users. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Supplier.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng suppliers. */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/SupplierRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng suppliers. */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Product.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng products. */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "has_expiry", nullable = false)
    private boolean hasExpiry;

    @Column(name = "expiry_alert_days", nullable = false)
    private int expiryAlertDays;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "storage_condition")
    private String storageCondition;

    @Column(name = "min_stock", nullable = false, precision = 18, scale = 3)
    private BigDecimal minStock;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/ProductRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng products. */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Location.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng locations. */
@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Column(name = "storage_condition")
    private String storageCondition;

    @Column(name = "is_locked", nullable = false)
    private boolean locked;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/LocationRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng locations. */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Batch.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.BatchStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng batches. */
@Entity
@Table(name = "batches")
@Getter
@Setter
@NoArgsConstructor
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "batch_no", nullable = false, unique = true)
    private String batchNo;

    @Column(name = "mfg_date")
    private LocalDate mfgDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatus status;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/BatchRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng batches. */
@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Inventory.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng inventory. */
@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"batch_id", "location_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "qty_on_hand", nullable = false, precision = 18, scale = 3)
    private BigDecimal qtyOnHand;

    @Column(name = "qty_reserved", nullable = false, precision = 18, scale = 3)
    private BigDecimal qtyReserved;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/InventoryRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Inventory;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inventory. */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /** Khóa dòng tồn trong transaction duyệt đơn và giữ chỗ. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.id = :id")
    Optional<Inventory> findByIdForUpdate(@Param("id") Long id);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/InboundOrder.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.InboundStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng inbound_orders. */
@Entity
@Table(name = "inbound_orders")
@Getter
@Setter
@NoArgsConstructor
public class InboundOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InboundStatus status;

    @Column(name = "expected_at")
    private LocalDateTime expectedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/InboundOrderRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inbound_orders. */
@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/InboundItem.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.ItemCondition;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng inbound_items. */
@Entity
@Table(name = "inbound_items")
@Getter
@Setter
@NoArgsConstructor
public class InboundItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inbound_order_id", nullable = false)
    private InboundOrder inboundOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "batch_id", nullable = true)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    @Column(name = "qty_ordered", nullable = false, precision = 18, scale = 3)
    private BigDecimal qtyOrdered;

    @Column(name = "qty_received", nullable = false, precision = 18, scale = 3)
    private BigDecimal qtyReceived;

    @Column(name = "unit_cost", nullable = false, precision = 18, scale = 3)
    private BigDecimal unitCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition")
    private ItemCondition itemCondition;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/InboundItemRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.InboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inbound_items. */
@Repository
public interface InboundItemRepository extends JpaRepository<InboundItem, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/OutboundOrder.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Priority;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng outbound_orders. */
@Entity
@Table(name = "outbound_orders")
@Getter
@Setter
@NoArgsConstructor
public class OutboundOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbound_order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "delivery_deadline")
    private LocalDateTime deliveryDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboundStatus status;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/OutboundOrderRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.OutboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng outbound_orders. */
@Repository
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/OutboundItem.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng outbound_items. */
@Entity
@Table(name = "outbound_items")
@Getter
@Setter
@NoArgsConstructor
public class OutboundItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbound_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outbound_order_id", nullable = false)
    private OutboundOrder outboundOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "inventory_id", nullable = true)
    private Inventory inventory;

    @Column(name = "qty_requested", nullable = false, precision = 18, scale = 3)
    private BigDecimal qtyRequested;

    @Column(name = "qty_picked", nullable = false, precision = 18, scale = 3)
    private BigDecimal qtyPicked;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 3)
    private BigDecimal unitPrice;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/OutboundItemRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.OutboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng outbound_items. */
@Repository
public interface OutboundItemRepository extends JpaRepository<OutboundItem, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Shipment.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.ShipmentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng shipments. */
@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    @Column(name = "carrier_name", nullable = false)
    private String carrierName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outbound_order_id", nullable = false)
    private OutboundOrder outboundOrder;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "driver_phone")
    private String driverPhone;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "driver_id", nullable = true)
    private User driver;

    @Column(name = "tracking_no")
    private String trackingNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/ShipmentRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng shipments. */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/Stocktake.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.StocktakeStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng stocktakes. */
@Entity
@Table(name = "stocktakes")
@Getter
@Setter
@NoArgsConstructor
public class Stocktake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stocktake_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name = "expected_qty", nullable = false, precision = 18, scale = 3)
    private BigDecimal expectedQty;

    @Column(name = "counted_qty", precision = 18, scale = 3)
    private BigDecimal countedQty;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StocktakeStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "approved_by", nullable = true)
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/StocktakeRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.Stocktake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng stocktakes. */
@Repository
public interface StocktakeRepository extends JpaRepository<Stocktake, Long> {
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/entity/InventoryMovement.java' <<'WAREHOUSE_FILE'
package com.warehouse.entity;

import com.warehouse.entity.enums.MovementType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/** Ánh xạ bảng inventory_movements, lưu các bút toán chỉ ghi thêm. */
@Entity
@Table(name = "inventory_movements")
@Getter
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "from_location_id", nullable = true)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "to_location_id", nullable = true)
    private Location toLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "reference_no", nullable = false)
    private String referenceNo;

    @Column(name = "change_qty", nullable = false, precision = 18, scale = 3)
    private BigDecimal changeQty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/repository/InventoryMovementRepository.java' <<'WAREHOUSE_FILE'
package com.warehouse.repository;

import com.warehouse.entity.InventoryMovement;
import com.warehouse.entity.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Đọc và ghi thêm sổ kho, chỉ công bố hai thao tác. */
@Repository
public interface InventoryMovementRepository extends org.springframework.data.repository.Repository<InventoryMovement, Long> {
    Optional<InventoryMovement> findById(Long id);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO inventory_movements
                (batch_id, from_location_id, to_location_id, movement_type,
                 reference_no, change_qty, created_by, created_at)
            VALUES (:batchId, :fromId, :toId, :#{#type.name()},
                    :reference, :delta, :actorId, :at)
            """, nativeQuery = true)
    int append(@Param("batchId") Long batchId, @Param("fromId") Long fromId,
               @Param("toId") Long toId, @Param("type") MovementType type,
               @Param("reference") String reference, @Param("delta") BigDecimal delta,
               @Param("actorId") Long actorId, @Param("at") LocalDateTime at);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/AuthRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Dữ liệu đầu vào của phân hệ xác thực. */
public final class AuthRequest {
    private AuthRequest() {}

    public record Login(
            @NotBlank String username,
            @NotBlank String password) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/AuthResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ xác thực. */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/AuthController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;
import com.warehouse.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ xác thực. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    /** Kiểm tra tiến trình HTTP của ứng dụng. */
    @GetMapping("/health")
    @PreAuthorize("permitAll()")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @PostMapping("/auth/login")
    @PreAuthorize("permitAll()")
    public AuthResponse login(@Valid @RequestBody AuthRequest.Login request) {
        return service.login(request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/AuthService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;

/** Hợp đồng nghiệp vụ cho phân hệ xác thực. */
public interface AuthService {
    AuthResponse login(AuthRequest.Login request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/AuthServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.AuthRequest;
import com.warehouse.dto.response.AuthResponse;
import com.warehouse.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ xác thực. */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse login(AuthRequest.Login request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/UserRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import com.warehouse.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Dữ liệu đầu vào của phân hệ người dùng. */
public final class UserRequest {
    private UserRequest() {}

    public record Create(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String fullName,
            @Email String email,
            String phone,
            String addr,
            @NotNull Role role) {}

    public record Update(
            @NotBlank String fullName,
            @Email String email,
            String phone,
            String addr,
            @NotNull Role role,
            boolean active) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/UserResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.Role;

/** Dữ liệu trả về của phân hệ người dùng. */
public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String addr,
        Role role,
        boolean active) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/UserMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import com.warehouse.entity.User;

/** Hợp đồng chuyển người dùng sang DTO công khai. */
public interface UserMapper {
    UserResponse toResponse(User entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    User toEntity(UserRequest.Create request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/UserController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import com.warehouse.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ người dùng. */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return service.listUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(@PathVariable("id") Long id) {
        return service.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody UserRequest.Create request) {
        return service.createUser(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserRequest.Update request) {
        return service.updateUser(id, request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/UserService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ người dùng. */
public interface UserService {
    List<UserResponse> listUsers();

    UserResponse getUser(Long id);

    UserResponse createUser(UserRequest.Create request);

    UserResponse updateUser(Long id, UserRequest.Update request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/UserServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import com.warehouse.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ người dùng. */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public UserResponse createUser(UserRequest.Create request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest.Update request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/ProductRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Dữ liệu đầu vào của phân hệ sản phẩm. */
public final class ProductRequest {
    private ProductRequest() {}

    public record Write(
            @NotBlank String skuCode,
            @NotBlank String name,
            @NotBlank String category,
            @NotBlank String unit,
            boolean hasExpiry,
            @PositiveOrZero int expiryAlertDays,
            String storageCondition,
            @NotNull @PositiveOrZero BigDecimal minStock,
            boolean active) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/ProductResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import java.math.BigDecimal;

/** Dữ liệu trả về của phân hệ sản phẩm. */
public record ProductResponse(
        Long id,
        String skuCode,
        String name,
        String category,
        String unit,
        boolean hasExpiry,
        int expiryAlertDays,
        BigDecimal minStock,
        String storageCondition,
        boolean active) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/ProductMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.Product;

/** Hợp đồng chuyển sản phẩm sang DTO công khai. */
public interface ProductMapper {
    ProductResponse toResponse(Product entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Product toEntity(ProductRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/ProductController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ sản phẩm. */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<ProductResponse> listProducts() {
        return service.listProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public ProductResponse getProduct(@PathVariable("id") Long id) {
        return service.getProduct(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest.Write request) {
        return service.createProduct(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ProductResponse updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductRequest.Write request) {
        return service.updateProduct(id, request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/ProductService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ sản phẩm. */
public interface ProductService {
    List<ProductResponse> listProducts();

    ProductResponse getProduct(Long id);

    ProductResponse createProduct(ProductRequest.Write request);

    ProductResponse updateProduct(Long id, ProductRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/ProductServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ sản phẩm. */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public ProductResponse createProduct(ProductRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/SupplierRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Dữ liệu đầu vào của phân hệ nhà cung cấp. */
public final class SupplierRequest {
    private SupplierRequest() {}

    public record Write(
            @NotBlank String name,
            String phone,
            String address,
            String licenseNo,
            boolean active) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/SupplierResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ nhà cung cấp. */
public record SupplierResponse(
        Long id,
        String name,
        String phone,
        String address,
        String licenseNo,
        boolean active) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/SupplierMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.entity.Supplier;

/** Hợp đồng chuyển nhà cung cấp sang DTO công khai. */
public interface SupplierMapper {
    SupplierResponse toResponse(Supplier entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Supplier toEntity(SupplierRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/SupplierController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.service.SupplierService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ nhà cung cấp. */
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<SupplierResponse> listSuppliers() {
        return service.listSuppliers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public SupplierResponse getSupplier(@PathVariable("id") Long id) {
        return service.getSupplier(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public SupplierResponse createSupplier(@Valid @RequestBody SupplierRequest.Write request) {
        return service.createSupplier(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public SupplierResponse updateSupplier(@PathVariable("id") Long id, @Valid @RequestBody SupplierRequest.Write request) {
        return service.updateSupplier(id, request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/SupplierService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ nhà cung cấp. */
public interface SupplierService {
    List<SupplierResponse> listSuppliers();

    SupplierResponse getSupplier(Long id);

    SupplierResponse createSupplier(SupplierRequest.Write request);

    SupplierResponse updateSupplier(Long id, SupplierRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/SupplierServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.SupplierRequest;
import com.warehouse.dto.response.SupplierResponse;
import com.warehouse.service.SupplierService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ nhà cung cấp. */
@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> listSuppliers() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplier(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/LocationRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Dữ liệu đầu vào của phân hệ vị trí lưu trữ. */
public final class LocationRequest {
    private LocationRequest() {}

    public record Write(
            @NotBlank String code,
            @NotBlank String warehouseName,
            @NotBlank String zoneName,
            String storageCondition,
            boolean active) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/LocationResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

/** Dữ liệu trả về của phân hệ vị trí lưu trữ. */
public record LocationResponse(
        Long id,
        String code,
        String warehouseName,
        String zoneName,
        String storageCondition,
        boolean locked,
        boolean active) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/LocationMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.entity.Location;

/** Hợp đồng chuyển vị trí lưu trữ sang DTO công khai. */
public interface LocationMapper {
    LocationResponse toResponse(Location entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Location toEntity(LocationRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/LocationController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.service.LocationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ vị trí lưu trữ. */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<LocationResponse> listLocations() {
        return service.listLocations();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public LocationResponse getLocation(@PathVariable("id") Long id) {
        return service.getLocation(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public LocationResponse createLocation(@Valid @RequestBody LocationRequest.Write request) {
        return service.createLocation(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public LocationResponse updateLocation(@PathVariable("id") Long id, @Valid @RequestBody LocationRequest.Write request) {
        return service.updateLocation(id, request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/LocationService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ vị trí lưu trữ. */
public interface LocationService {
    List<LocationResponse> listLocations();

    LocationResponse getLocation(Long id);

    LocationResponse createLocation(LocationRequest.Write request);

    LocationResponse updateLocation(Long id, LocationRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/LocationServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.service.LocationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ vị trí lưu trữ. */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> listLocations() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public LocationResponse createLocation(LocationRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public LocationResponse updateLocation(Long id, LocationRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/BatchRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import com.warehouse.entity.enums.BatchStatus;
import jakarta.validation.constraints.Positive;

/** Dữ liệu đầu vào của phân hệ lô hàng. */
public final class BatchRequest {
    private BatchRequest() {}

    public record Search(
            @Positive Long productId,
            BatchStatus status) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/BatchResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.BatchStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ lô hàng. */
public record BatchResponse(
        Long id,
        Long productId,
        Long supplierId,
        String batchNo,
        LocalDate mfgDate,
        LocalDate expiryDate,
        LocalDateTime receivedAt,
        BatchStatus status) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/BatchMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.response.BatchResponse;
import com.warehouse.entity.Batch;

/** Hợp đồng chuyển lô hàng sang DTO công khai. */
public interface BatchMapper {
    BatchResponse toResponse(Batch entity);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/BatchController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.BatchService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ lô hàng. */
@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<BatchResponse> listBatches(@Valid @ModelAttribute BatchRequest.Search request) {
        return service.listBatches(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public BatchResponse getBatch(@PathVariable("id") Long id) {
        return service.getBatch(id);
    }

    @GetMapping("/{id}/trace")
    @PreAuthorize("hasRole('MANAGER')")
    public List<MovementResponse> traceBatch(@PathVariable("id") Long id) {
        return service.traceBatch(id);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/BatchService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ lô hàng. */
public interface BatchService {
    List<BatchResponse> listBatches(BatchRequest.Search request);

    BatchResponse getBatch(Long id);

    List<MovementResponse> traceBatch(Long id);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/BatchServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.BatchService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ lô hàng. */
@Service
@Transactional
public class BatchServiceImpl implements BatchService {
    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> listBatches(BatchRequest.Search request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponse getBatch(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> traceBatch(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/InventoryRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.Positive;

/** Dữ liệu đầu vào của phân hệ tồn kho. */
public final class InventoryRequest {
    private InventoryRequest() {}

    public record Search(
            @Positive Long batchId,
            @Positive Long locationId) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/InventoryResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ tồn kho. */
public record InventoryResponse(
        Long id,
        Long batchId,
        Long locationId,
        BigDecimal qtyOnHand,
        BigDecimal qtyReserved,
        LocalDateTime updatedAt) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/InventoryMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.entity.Inventory;

/** Hợp đồng chuyển tồn kho sang DTO công khai. */
public interface InventoryMapper {
    InventoryResponse toResponse(Inventory entity);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/InventoryController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ tồn kho. */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<InventoryResponse> listInventory(@Valid @ModelAttribute InventoryRequest.Search request) {
        return service.listInventory(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InventoryResponse getInventory(@PathVariable("id") Long id) {
        return service.getInventory(id);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/InventoryService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ tồn kho. */
public interface InventoryService {
    List<InventoryResponse> listInventory(InventoryRequest.Search request);

    InventoryResponse getInventory(Long id);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/InventoryServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.InventoryRequest;
import com.warehouse.dto.response.InventoryResponse;
import com.warehouse.service.InventoryService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ tồn kho. */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {
    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory(InventoryRequest.Search request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/InboundOrderRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import com.warehouse.entity.enums.ItemCondition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu đầu vào của phân hệ phiếu nhập. */
public final class InboundOrderRequest {
    private InboundOrderRequest() {}

    public record Write(
            @NotNull @Positive Long supplierId,
            LocalDateTime expectedAt,
            @NotEmpty List<@NotNull @Valid Line> items) {}

    public record Line(
            @NotNull @Positive Long productId,
            @NotNull @Positive BigDecimal qtyOrdered,
            @NotNull @PositiveOrZero BigDecimal unitCost) {}

    public record Receive(
            @NotEmpty List<@NotNull @Valid ReceivedLine> items) {}

    public record ReceivedLine(
            @NotNull @Positive Long inboundItemId,
            @NotNull @Positive Long locationId,
            @NotNull @PositiveOrZero BigDecimal qtyReceived,
            @NotNull ItemCondition itemCondition,
            String batchNo,
            LocalDate mfgDate,
            LocalDate expiryDate,
            String discrepancyReason) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/InboundOrderResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.InboundStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu trả về của phân hệ phiếu nhập. */
public record InboundOrderResponse(
        Long id,
        String orderNo,
        Long supplierId,
        InboundStatus status,
        LocalDateTime expectedAt,
        List<Line> items) {
    public record Line(
            Long id,
            Long productId,
            Long batchId,
            Long locationId,
            BigDecimal qtyOrdered,
            BigDecimal qtyReceived,
            BigDecimal unitCost) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/InboundOrderMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.entity.InboundOrder;

/** Hợp đồng chuyển phiếu nhập sang DTO công khai. */
public interface InboundOrderMapper {
    InboundOrderResponse toResponse(InboundOrder entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    InboundOrder toEntity(InboundOrderRequest.Write request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/InboundOrderController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.service.InboundOrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ phiếu nhập. */
@RestController
@RequestMapping("/api/v1/inbound-orders")
@RequiredArgsConstructor
public class InboundOrderController {
    private final InboundOrderService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<InboundOrderResponse> listInboundOrders() {
        return service.listInboundOrders();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse createInbound(@Valid @RequestBody InboundOrderRequest.Write request) {
        return service.createInbound(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse getInbound(@PathVariable("id") Long id) {
        return service.getInbound(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse updateInbound(@PathVariable("id") Long id, @Valid @RequestBody InboundOrderRequest.Write request) {
        return service.updateInbound(id, request);
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public InboundOrderResponse receiveInbound(@PathVariable("id") Long id, @Valid @RequestBody InboundOrderRequest.Receive request) {
        return service.receiveInbound(id, request);
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('MANAGER')")
    public InboundOrderResponse confirmInbound(@PathVariable("id") Long id) {
        return service.confirmInbound(id);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/InboundOrderService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ phiếu nhập. */
public interface InboundOrderService {
    List<InboundOrderResponse> listInboundOrders();

    InboundOrderResponse createInbound(InboundOrderRequest.Write request);

    InboundOrderResponse getInbound(Long id);

    InboundOrderResponse updateInbound(Long id, InboundOrderRequest.Write request);

    InboundOrderResponse receiveInbound(Long id, InboundOrderRequest.Receive request);

    /** Tạo lô, cộng tồn, ghi sổ và xác nhận trong cùng transaction. */
    InboundOrderResponse confirmInbound(Long id);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/InboundOrderServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.service.InboundOrderService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ phiếu nhập. */
@Service
@Transactional
public class InboundOrderServiceImpl implements InboundOrderService {
    @Override
    @Transactional(readOnly = true)
    public List<InboundOrderResponse> listInboundOrders() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse createInbound(InboundOrderRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public InboundOrderResponse getInbound(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse updateInbound(Long id, InboundOrderRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse receiveInbound(Long id, InboundOrderRequest.Receive request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse confirmInbound(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/OutboundOrderRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import com.warehouse.entity.enums.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu đầu vào của phân hệ đơn xuất. */
public final class OutboundOrderRequest {
    private OutboundOrderRequest() {}

    public record Create(
            @NotNull Priority priority,
            LocalDateTime deliveryDeadline,
            @NotBlank String shippingAddress,
            @NotEmpty List<@NotNull @Valid Line> items) {}

    public record Line(
            @NotNull @Positive Long productId,
            @NotNull @Positive BigDecimal qtyRequested) {}

    public record Pick(
            @NotEmpty List<@NotNull @Valid PickedLine> items) {}

    public record PickedLine(
            @NotNull @Positive Long outboundItemId,
            @NotNull @Positive Long inventoryId,
            @NotNull @Positive BigDecimal qtyPicked,
            String overrideReason) {}

    public record Handover(
            @NotNull @Positive Long driverId,
            @NotBlank String receiverName,
            @NotBlank String carrierName,
            String trackingNo) {}

    public record Cancel(
            @NotBlank String reason) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/OutboundOrderResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Priority;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Dữ liệu trả về của phân hệ đơn xuất. */
public record OutboundOrderResponse(
        Long id,
        String orderNo,
        Priority priority,
        OutboundStatus status,
        String shippingAddress,
        LocalDateTime deliveryDeadline,
        List<Line> items) {
    public record Line(
            Long id,
            Long productId,
            BigDecimal qtyRequested,
            BigDecimal qtyPicked) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/OutboundOrderMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.entity.OutboundOrder;

/** Hợp đồng chuyển đơn xuất sang DTO công khai. */
public interface OutboundOrderMapper {
    OutboundOrderResponse toResponse(OutboundOrder entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    OutboundOrder toEntity(OutboundOrderRequest.Create request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/OutboundOrderController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.service.OutboundOrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ đơn xuất. */
@RestController
@RequestMapping("/api/v1/outbound-orders")
@RequiredArgsConstructor
public class OutboundOrderController {
    private final OutboundOrderService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'MANAGER')")
    public List<OutboundOrderResponse> listOrders(Principal actor) {
        return service.listOrders(actor);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public OutboundOrderResponse createOrder(@Valid @RequestBody OutboundOrderRequest.Create request, Principal actor) {
        return service.createOrder(request, actor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'MANAGER')")
    public OutboundOrderResponse getOrder(@PathVariable("id") Long id, Principal actor) {
        return service.getOrder(id, actor);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public OutboundOrderResponse approveOrder(@PathVariable("id") Long id) {
        return service.approveOrder(id);
    }

    @PostMapping("/{id}/pick")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse pickOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Pick request) {
        return service.pickOrder(id, request);
    }

    @PostMapping("/{id}/pack")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse packOrder(@PathVariable("id") Long id) {
        return service.packOrder(id);
    }

    @PostMapping("/{id}/handover")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public OutboundOrderResponse handoverOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Handover request) {
        return service.handoverOrder(id, request);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public OutboundOrderResponse cancelOrder(@PathVariable("id") Long id, @Valid @RequestBody OutboundOrderRequest.Cancel request) {
        return service.cancelOrder(id, request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/OutboundOrderService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import java.security.Principal;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ đơn xuất. */
public interface OutboundOrderService {
    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    List<OutboundOrderResponse> listOrders(Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    OutboundOrderResponse createOrder(OutboundOrderRequest.Create request, Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    OutboundOrderResponse getOrder(Long id, Principal actor);

    /** Khóa dòng tồn trước khi giữ chỗ trong cùng transaction. */
    OutboundOrderResponse approveOrder(Long id);

    OutboundOrderResponse pickOrder(Long id, OutboundOrderRequest.Pick request);

    OutboundOrderResponse packOrder(Long id);

    /** Trừ tồn, giải phóng giữ chỗ và ghi nhận bàn giao trong cùng transaction. */
    OutboundOrderResponse handoverOrder(Long id, OutboundOrderRequest.Handover request);

    OutboundOrderResponse cancelOrder(Long id, OutboundOrderRequest.Cancel request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/OutboundOrderServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.service.OutboundOrderService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ đơn xuất. */
@Service
@Transactional
public class OutboundOrderServiceImpl implements OutboundOrderService {
    @Override
    @Transactional(readOnly = true)
    public List<OutboundOrderResponse> listOrders(Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse createOrder(OutboundOrderRequest.Create request, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public OutboundOrderResponse getOrder(Long id, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse approveOrder(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse pickOrder(Long id, OutboundOrderRequest.Pick request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse packOrder(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse handoverOrder(Long id, OutboundOrderRequest.Handover request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse cancelOrder(Long id, OutboundOrderRequest.Cancel request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/ShipmentRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

/** Dữ liệu đầu vào của phân hệ chuyến giao hàng. */
public final class ShipmentRequest {
    private ShipmentRequest() {}

    public record Receipt(
            String note) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/ShipmentResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.ShipmentStatus;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ chuyến giao hàng. */
public record ShipmentResponse(
        Long id,
        Long outboundOrderId,
        String carrierName,
        String trackingNo,
        ShipmentStatus status,
        LocalDateTime dispatchedAt,
        LocalDateTime deliveredAt) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/ShipmentMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.entity.Shipment;

/** Hợp đồng chuyển chuyến giao hàng sang DTO công khai. */
public interface ShipmentMapper {
    ShipmentResponse toResponse(Shipment entity);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/ShipmentController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.service.ShipmentService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ chuyến giao hàng. */
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService service;

    @GetMapping("/mine")
    @PreAuthorize("hasRole('DRIVER')")
    public List<ShipmentResponse> myShipments(Principal actor) {
        return service.myShipments(actor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ShipmentResponse getShipment(@PathVariable("id") Long id, Principal actor) {
        return service.getShipment(id, actor);
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ShipmentResponse acceptShipment(@PathVariable("id") Long id, @Valid @RequestBody ShipmentRequest.Receipt request, Principal actor) {
        return service.acceptShipment(id, request, actor);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/ShipmentService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import java.security.Principal;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ chuyến giao hàng. */
public interface ShipmentService {
    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    List<ShipmentResponse> myShipments(Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    ShipmentResponse getShipment(Long id, Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    ShipmentResponse acceptShipment(Long id, ShipmentRequest.Receipt request, Principal actor);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/ShipmentServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import com.warehouse.service.ShipmentService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ chuyến giao hàng. */
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {
    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> myShipments(Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long id, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public ShipmentResponse acceptShipment(Long id, ShipmentRequest.Receipt request, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/StocktakeRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Dữ liệu đầu vào của phân hệ kiểm kê. */
public final class StocktakeRequest {
    private StocktakeRequest() {}

    public record Create(
            @NotNull @Positive Long locationId,
            @NotNull @Positive Long batchId) {}

    public record Count(
            @NotNull @PositiveOrZero BigDecimal countedQty,
            String reason) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/StocktakeResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.StocktakeStatus;
import java.math.BigDecimal;

/** Dữ liệu trả về của phân hệ kiểm kê. */
public record StocktakeResponse(
        Long id,
        Long locationId,
        Long batchId,
        BigDecimal expectedQty,
        BigDecimal countedQty,
        StocktakeStatus status,
        String reason) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/StocktakeMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.entity.Stocktake;

/** Hợp đồng chuyển kiểm kê sang DTO công khai. */
public interface StocktakeMapper {
    StocktakeResponse toResponse(Stocktake entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    Stocktake toEntity(StocktakeRequest.Create request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/StocktakeController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.service.StocktakeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ kiểm kê. */
@RestController
@RequestMapping("/api/v1/stocktakes")
@RequiredArgsConstructor
public class StocktakeController {
    private final StocktakeService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<StocktakeResponse> listStocktakes() {
        return service.listStocktakes();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse createStocktake(@Valid @RequestBody StocktakeRequest.Create request) {
        return service.createStocktake(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse getStocktake(@PathVariable("id") Long id) {
        return service.getStocktake(id);
    }

    @PostMapping("/{id}/count")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public StocktakeResponse recordCount(@PathVariable("id") Long id, @Valid @RequestBody StocktakeRequest.Count request) {
        return service.recordCount(id, request);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public StocktakeResponse approveAdjustment(@PathVariable("id") Long id) {
        return service.approveAdjustment(id);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/StocktakeService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ kiểm kê. */
public interface StocktakeService {
    List<StocktakeResponse> listStocktakes();

    StocktakeResponse createStocktake(StocktakeRequest.Create request);

    StocktakeResponse getStocktake(Long id);

    StocktakeResponse recordCount(Long id, StocktakeRequest.Count request);

    StocktakeResponse approveAdjustment(Long id);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/StocktakeServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.service.StocktakeService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ kiểm kê. */
@Service
@Transactional
public class StocktakeServiceImpl implements StocktakeService {
    @Override
    @Transactional(readOnly = true)
    public List<StocktakeResponse> listStocktakes() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public StocktakeResponse createStocktake(StocktakeRequest.Create request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public StocktakeResponse getStocktake(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public StocktakeResponse recordCount(Long id, StocktakeRequest.Count request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public StocktakeResponse approveAdjustment(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/MovementRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Dữ liệu đầu vào của phân hệ biến động kho. */
public final class MovementRequest {
    private MovementRequest() {}

    public record Transfer(
            @NotNull @Positive Long batchId,
            @NotNull @Positive Long fromLocationId,
            @NotNull @Positive Long toLocationId,
            @NotNull @Positive BigDecimal quantity,
            String reason) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/MovementResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import com.warehouse.entity.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Dữ liệu trả về của phân hệ biến động kho. */
public record MovementResponse(
        Long id,
        Long batchId,
        Long fromLocationId,
        Long toLocationId,
        MovementType movementType,
        String referenceNo,
        BigDecimal changeQty,
        LocalDateTime createdAt) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/mapper/MovementMapper.java' <<'WAREHOUSE_FILE'
package com.warehouse.mapper;

import com.warehouse.dto.response.MovementResponse;
import com.warehouse.entity.InventoryMovement;

/** Hợp đồng chuyển biến động kho sang DTO công khai. */
public interface MovementMapper {
    MovementResponse toResponse(InventoryMovement entity);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/MovementController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.MovementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ biến động kho. */
@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {
    private final MovementService service;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public List<MovementResponse> listMovements() {
        return service.listMovements();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public MovementResponse transferStock(@Valid @RequestBody MovementRequest.Transfer request) {
        return service.transferStock(request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/MovementService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ biến động kho. */
public interface MovementService {
    List<MovementResponse> listMovements();

    MovementResponse transferStock(MovementRequest.Transfer request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/MovementServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.service.MovementService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ biến động kho. */
@Service
@Transactional
public class MovementServiceImpl implements MovementService {
    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> listMovements() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public MovementResponse transferStock(MovementRequest.Transfer request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/request/ReportRequest.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Dữ liệu đầu vào của phân hệ báo cáo kho. */
public final class ReportRequest {
    private ReportRequest() {}

    public record Period(
            @NotNull LocalDate from,
            @NotNull LocalDate to) {}
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/dto/response/ReportResponse.java' <<'WAREHOUSE_FILE'
package com.warehouse.dto.response;

import java.math.BigDecimal;

/** Dữ liệu trả về của phân hệ báo cáo kho. */
public record ReportResponse(
        Long productId,
        String skuCode,
        BigDecimal openingQty,
        BigDecimal inboundQty,
        BigDecimal outboundQty,
        BigDecimal netAdjustment,
        BigDecimal closingQty) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/controller/ReportController.java' <<'WAREHOUSE_FILE'
package com.warehouse.controller;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import com.warehouse.service.ReportService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ báo cáo kho. */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @GetMapping("/stock-summary")
    @PreAuthorize("hasRole('MANAGER')")
    public List<ReportResponse> stockSummary(@Valid @ModelAttribute ReportRequest.Period request) {
        return service.stockSummary(request);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/ReportService.java' <<'WAREHOUSE_FILE'
package com.warehouse.service;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ báo cáo kho. */
public interface ReportService {
    List<ReportResponse> stockSummary(ReportRequest.Period request);
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/service/impl/ReportServiceImpl.java' <<'WAREHOUSE_FILE'
package com.warehouse.service.impl;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import com.warehouse.service.ReportService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ báo cáo kho. */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {
    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> stockSummary(ReportRequest.Period request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/WarehouseApplication.java' <<'WAREHOUSE_FILE'
package com.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Điểm khởi động của hệ thống quản lý kho tập trung. */
@SpringBootApplication
public class WarehouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/config/WebConfig.java' <<'WAREHOUSE_FILE'
package com.warehouse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Cấu hình Spring MVC và tài nguyên giao diện tĩnh. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot phục vụ HTML, CSS và JS từ classpath:/static/.
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/config/JwtConfig.java' <<'WAREHOUSE_FILE'
package com.warehouse.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Thông số ký và thời hạn tối đa của access token. */
@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(@NotBlank String secret, @Positive long expirationSeconds) {}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/security/JwtAuthFilter.java' <<'WAREHOUSE_FILE'
package com.warehouse.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

/** Xác thực Bearer token qua bộ lọc chuẩn của Spring Security. */
public class JwtAuthFilter extends BearerTokenAuthenticationFilter {
    public JwtAuthFilter(AuthenticationManager manager) {
        super(manager);
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/security/CustomUserDetails.java' <<'WAREHOUSE_FILE'
package com.warehouse.security;

import com.warehouse.entity.enums.Role;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Biểu diễn danh tính và vai trò của người dùng đã xác thực. */
public class CustomUserDetails extends User {
    public CustomUserDetails(String username, Role role) {
        super(username, "", List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/security/JwtService.java' <<'WAREHOUSE_FILE'
package com.warehouse.security;

import com.warehouse.config.JwtConfig;
import com.warehouse.entity.enums.Role;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

/** Kiểm tra chữ ký, thời hạn và danh tính của JWT dùng cho API. */
@Component
public class JwtService {
    private static final Set<String> ROLES = Arrays.stream(Role.values())
            .map(Role::name).collect(Collectors.toUnmodifiableSet());
    private final JwtConfig config;

    public JwtService(JwtConfig config) {
        this.config = config;
    }

    public JwtDecoder decoder() {
        byte[] key = Base64.getDecoder().decode(config.secret());
        if (key.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET cần ít nhất 32 byte sau khi giải mã Base64");
        }
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256")).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer("warehouse"), this::validateClaims));
        return decoder;
    }

    private OAuth2TokenValidatorResult validateClaims(Jwt jwt) {
        Instant issued = jwt.getIssuedAt();
        Instant expires = jwt.getExpiresAt();
        Object role = jwt.getClaims().get("role");
        if (issued == null || expires == null || !expires.isAfter(issued)
                || issued.isAfter(Instant.now().plusSeconds(60))
                || Duration.between(issued, expires).compareTo(Duration.ofSeconds(config.expirationSeconds())) > 0
                || jwt.getSubject() == null || jwt.getSubject().isBlank()
                || !(role instanceof String name) || !ROLES.contains(name)) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        }
        return OAuth2TokenValidatorResult.success();
    }

    public UsernamePasswordAuthenticationToken authentication(Jwt jwt) {
        var user = new CustomUserDetails(jwt.getSubject(), Role.valueOf(jwt.getClaimAsString("role")));
        return UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/exception/ErrorCode.java' <<'WAREHOUSE_FILE'
package com.warehouse.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** Mã lỗi ổn định và trạng thái HTTP tương ứng. */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Chưa cài đặt"),
    ALREADY_CONFIRMED(HttpStatus.CONFLICT, "Phiếu đã được xác nhận"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Số lượng vượt tồn khả dụng"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Cần xác thực"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Không đủ quyền thực hiện");

    private final HttpStatus status;
    private final String message;
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/exception/AppException.java' <<'WAREHOUSE_FILE'
package com.warehouse.exception;

import lombok.Getter;

/** Mang mã lỗi nghiệp vụ từ service đến bộ xử lý lỗi HTTP. */
@Getter
public class AppException extends RuntimeException {
    private final ErrorCode code;

    public AppException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/exception/GlobalExceptionHandler.java' <<'WAREHOUSE_FILE'
package com.warehouse.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Chuẩn hóa lỗi API và chi tiết kiểm tra dữ liệu theo từng trường. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorBody(String code, String message, Map<String, String> fields) {}
    public record Envelope(ErrorBody error) {}

    public static Envelope envelope(ErrorCode code, Map<String, String> fields) {
        return new Envelope(new ErrorBody(code.name(), code.getMessage(), fields));
    }

    private ResponseEntity<Envelope> response(ErrorCode code, Map<String, String> fields) {
        return ResponseEntity.status(code.getStatus()).body(envelope(code, fields));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Envelope> notImplemented() {
        return response(ErrorCode.NOT_IMPLEMENTED, Map.of());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Envelope> applicationError(AppException exception) {
        return response(exception.getCode(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Envelope> invalidFields(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(ErrorCode.VALIDATION_ERROR, fields);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Envelope> invalidInput() {
        return response(ErrorCode.VALIDATION_ERROR, Map.of());
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/config/SecurityConfig.java' <<'WAREHOUSE_FILE'
package com.warehouse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.exception.ErrorCode;
import com.warehouse.exception.GlobalExceptionHandler;
import com.warehouse.security.JwtAuthFilter;
import com.warehouse.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Thiết lập xác thực stateless, BCrypt và phân quyền ở controller. */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtConfig.class)
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtService jwt, ObjectMapper json) throws Exception {
        var provider = new JwtAuthenticationProvider(jwt.decoder());
        provider.setJwtAuthenticationConverter(jwt::authentication);
        var filter = new JwtAuthFilter(new ProviderManager(provider));
        AuthenticationEntryPoint unauthorized = (request, response, error) -> {
            response.setHeader("WWW-Authenticate", "Bearer");
            writeError(response, json, ErrorCode.UNAUTHORIZED);
        };
        filter.setAuthenticationEntryPoint(unauthorized);
        return http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler((req, res, ex) -> writeError(res, json, ErrorCode.FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/pages/**", "/css/**", "/js/**",
                                "/error", "/api/v1/health", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().denyAll())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static void writeError(HttpServletResponse res, ObjectMapper json, ErrorCode code)
            throws IOException {
        res.setStatus(code.getStatus().value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        json.writeValue(res.getOutputStream(), GlobalExceptionHandler.envelope(code, Map.of()));
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/util/BatchNoGenerator.java' <<'WAREHOUSE_FILE'
package com.warehouse.util;

import java.time.LocalDate;

/** Điểm mở rộng để sinh mã lô hàng. */
public final class BatchNoGenerator {
    private BatchNoGenerator() {}

    public static String generate(String skuCode, LocalDate mfgDate) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'src/main/java/com/warehouse/util/PageUtil.java' <<'WAREHOUSE_FILE'
package com.warehouse.util;

import org.springframework.data.domain.Pageable;

/** Điểm mở rộng để chuẩn hóa tham số phân trang. */
public final class PageUtil {
    private PageUtil() {}

    public static Pageable pageable(int page, int size) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
WAREHOUSE_FILE

    cat > 'pom.xml' <<'WAREHOUSE_FILE'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.16</version>
        <relativePath/>
    </parent>
    <groupId>com.warehouse</groupId>
    <artifactId>warehouse</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>warehouse</name>
    <properties>
        <java.version>17</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!-- Chỉ cho goal spring-boot:run, JAR dùng application.yml. -->
        <spring-boot.run.profiles>dev</spring-boot.run.profiles>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-mysql</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
WAREHOUSE_FILE

    cat > '.gitignore' <<'WAREHOUSE_FILE'
target/
.idea/
*.iml
.classpath
.project
.settings/
.vscode/
.env
*.log
WAREHOUSE_FILE

    cat > 'Dockerfile' <<'WAREHOUSE_FILE'
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/warehouse-0.0.1-SNAPSHOT.jar /app/warehouse.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/warehouse.jar"]
WAREHOUSE_FILE

    cat > 'docker-compose.yml' <<'WAREHOUSE_FILE'
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_DATABASE: ${DB_NAME:-warehouse}
      MYSQL_USER: ${DB_USER:-warehouse}
      MYSQL_PASSWORD: ${DB_PASSWORD:?Set DB_PASSWORD}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:?Set MYSQL_ROOT_PASSWORD}
    ports:
      - "127.0.0.1:${DB_PORT:-3306}:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD-SHELL", "MYSQL_PWD=\"$${MYSQL_PASSWORD}\" mysql --protocol=TCP -h 127.0.0.1 -u \"$${MYSQL_USER}\" -D \"$${MYSQL_DATABASE}\" -e 'SELECT 1'"]
      interval: 5s
      timeout: 5s
      retries: 30
      start_period: 20s
  app:
    build: .
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: ${DB_NAME:-warehouse}
      DB_USER: ${DB_USER:-warehouse}
      DB_PASSWORD: ${DB_PASSWORD:?Set DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET:?Set JWT_SECRET}
      JWT_EXPIRATION_SECONDS: ${JWT_EXPIRATION_SECONDS:-3600}
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
    ports:
      - "127.0.0.1:8080:8080"
volumes:
  mysql_data: {}
WAREHOUSE_FILE

    cat > 'src/main/resources/application.yml' <<'WAREHOUSE_FILE'
spring:
  application:
    name: warehouse
  datasource:
    url: ${DB_URL:jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:warehouse}?connectionTimeZone=UTC&allowPublicKeyRetrieval=true}
    username: ${DB_USER:warehouse}
    password: ${DB_PASSWORD}
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
jwt:
  secret: ${JWT_SECRET}
  expiration-seconds: ${JWT_EXPIRATION_SECONDS:3600}
WAREHOUSE_FILE

    cat > 'src/main/resources/application-dev.yml' <<'WAREHOUSE_FILE'
# Khởi động khung trên MySQL trống. Flyway kết nối nhưng chưa áp dụng V1/V2.
spring:
  jpa:
    hibernate:
      ddl-auto: none
  flyway:
    target: "0"
WAREHOUSE_FILE

    : > 'src/main/resources/db/migration/V1__schema.sql'

    : > 'src/main/resources/db/migration/V2__seed.sql'

    cat > 'src/main/resources/static/index.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="./css/base.css">
    <link rel="stylesheet" href="./css/layout.css">
    <link rel="stylesheet" href="./css/components.css">
    <script type="module" src="./js/pages/index.js"></script>
</head>
<body>
    <main>
        <h1>Đăng nhập</h1>
        <form method="post" action="/api/v1/auth/login">
            <label for="username">Tên đăng nhập</label>
            <input id="username" name="username" autocomplete="username" required>
            <label for="password">Mật khẩu</label>
            <input id="password" name="password" type="password" autocomplete="current-password" required>
            <button type="submit" disabled>Đăng nhập</button>
        </form>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/index.js' <<'WAREHOUSE_FILE'
import "../auth.js";

// Khung JavaScript cho trang đăng nhập.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/dashboard.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tổng quan kho | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/dashboard.js"></script>
</head>
<body>
    <main>
        <h1>Tổng quan kho</h1>
        <nav aria-label="Phân hệ kho">
            <ul>
                <li><a href="users.html">Người dùng</a></li>
                <li><a href="products.html">Sản phẩm</a></li>
                <li><a href="suppliers.html">Nhà cung cấp</a></li>
                <li><a href="locations.html">Vị trí lưu trữ</a></li>
                <li><a href="inventory.html">Tồn kho</a></li>
                <li><a href="inbound-list.html">Danh sách phiếu nhập</a></li>
                <li><a href="inbound-form.html">Lập phiếu nhập</a></li>
                <li><a href="inbound-receive.html">Tiếp nhận hàng</a></li>
                <li><a href="inbound-detail.html">Chi tiết phiếu nhập</a></li>
                <li><a href="outbound-list.html">Danh sách đơn xuất</a></li>
                <li><a href="outbound-form.html">Tạo yêu cầu xuất</a></li>
                <li><a href="outbound-detail.html">Chi tiết đơn xuất</a></li>
                <li><a href="my-shipments.html">Chuyến giao của tôi</a></li>
                <li><a href="stocktake.html">Kiểm kê</a></li>
                <li><a href="transfer.html">Di chuyển lô</a></li>
                <li><a href="reports.html">Báo cáo kho</a></li>
                <li><a href="batch-trace.html">Truy vết lô hàng</a></li>
            </ul>
        </nav>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/dashboard.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang tổng quan kho.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/users.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Người dùng | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/users.js"></script>
</head>
<body>
    <main>
        <h1>Người dùng</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/users.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang người dùng.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/products.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sản phẩm | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/products.js"></script>
</head>
<body>
    <main>
        <h1>Sản phẩm</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/products.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang sản phẩm.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/suppliers.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nhà cung cấp | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/suppliers.js"></script>
</head>
<body>
    <main>
        <h1>Nhà cung cấp</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/suppliers.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang nhà cung cấp.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/locations.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Vị trí lưu trữ | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/locations.js"></script>
</head>
<body>
    <main>
        <h1>Vị trí lưu trữ</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/locations.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang vị trí lưu trữ.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/inventory.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tồn kho | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/inventory.js"></script>
</head>
<body>
    <main>
        <h1>Tồn kho</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/inventory.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang tồn kho.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/inbound-list.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Danh sách phiếu nhập | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/inbound-list.js"></script>
</head>
<body>
    <main>
        <h1>Danh sách phiếu nhập</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/inbound-list.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang danh sách phiếu nhập.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/inbound-form.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lập phiếu nhập | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/inbound-form.js"></script>
</head>
<body>
    <main>
        <h1>Lập phiếu nhập</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/inbound-form.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang lập phiếu nhập.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/inbound-receive.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tiếp nhận hàng | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/inbound-receive.js"></script>
</head>
<body>
    <main>
        <h1>Tiếp nhận hàng</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/inbound-receive.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang tiếp nhận hàng.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/inbound-detail.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết phiếu nhập | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/inbound-detail.js"></script>
</head>
<body>
    <main>
        <h1>Chi tiết phiếu nhập</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/inbound-detail.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang chi tiết phiếu nhập.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/outbound-list.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Danh sách đơn xuất | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/outbound-list.js"></script>
</head>
<body>
    <main>
        <h1>Danh sách đơn xuất</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/outbound-list.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang danh sách đơn xuất.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/outbound-form.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tạo yêu cầu xuất | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/outbound-form.js"></script>
</head>
<body>
    <main>
        <h1>Tạo yêu cầu xuất</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/outbound-form.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang tạo yêu cầu xuất.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/outbound-detail.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết đơn xuất | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/outbound-detail.js"></script>
</head>
<body>
    <main>
        <h1>Chi tiết đơn xuất</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/outbound-detail.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang chi tiết đơn xuất.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/my-shipments.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chuyến giao của tôi | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/my-shipments.js"></script>
</head>
<body>
    <main>
        <h1>Chuyến giao của tôi</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/my-shipments.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang chuyến giao của tôi.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/stocktake.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Kiểm kê | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/stocktake.js"></script>
</head>
<body>
    <main>
        <h1>Kiểm kê</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/stocktake.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang kiểm kê.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/transfer.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Di chuyển lô | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/transfer.js"></script>
</head>
<body>
    <main>
        <h1>Di chuyển lô</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/transfer.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang di chuyển lô.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/reports.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Báo cáo kho | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/reports.js"></script>
</head>
<body>
    <main>
        <h1>Báo cáo kho</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/reports.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang báo cáo kho.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/pages/batch-trace.html' <<'WAREHOUSE_FILE'
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Truy vết lô hàng | Quản lý kho tập trung</title>
    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/layout.css">
    <link rel="stylesheet" href="../css/components.css">
    <script type="module" src="../js/pages/batch-trace.js"></script>
</head>
<body>
    <main>
        <h1>Truy vết lô hàng</h1>
        <a href="dashboard.html">Về tổng quan kho</a>
    </main>
</body>
</html>
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/pages/batch-trace.js' <<'WAREHOUSE_FILE'
// Khung JavaScript cho trang truy vết lô hàng.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/config.js' <<'WAREHOUSE_FILE'
export const API_BASE_URL = "/api/v1";
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/api.js' <<'WAREHOUSE_FILE'
export { API_BASE_URL } from "./config.js";

// Bổ sung thao tác REST/JSON khi triển khai giao diện.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/auth.js' <<'WAREHOUSE_FILE'
// Điểm tích hợp đăng nhập và đăng xuất của giao diện.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/js/mock.js' <<'WAREHOUSE_FILE'
// Chỗ dành cho dữ liệu mẫu của giao diện.
WAREHOUSE_FILE

    cat > 'src/main/resources/static/css/base.css' <<'WAREHOUSE_FILE'
html { font-family: system-ui, sans-serif; }
body { margin: 0; color: #17272a; }
WAREHOUSE_FILE

    cat > 'src/main/resources/static/css/layout.css' <<'WAREHOUSE_FILE'
main { max-width: 64rem; margin: 2rem auto; padding: 0 1rem; }
form { display: grid; gap: .65rem; max-width: 24rem; }
WAREHOUSE_FILE

    cat > 'src/main/resources/static/css/components.css' <<'WAREHOUSE_FILE'
input, button { font: inherit; padding: .5rem; }
a { color: #205b68; }
WAREHOUSE_FILE

    cat > 'README.md' <<'WAREHOUSE_FILE'
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
WAREHOUSE_FILE
)
# GNU mv -Tn (Linux/WSL) tránh ghi đè khi hai tiến trình cùng chạy.
mv -Tn -- "$stage" warehouse
if [[ -d "$stage" ]]; then
    echo 'Một tiến trình khác đã tạo warehouse, kết thúc để bảo toàn dữ liệu.' >&2
    exit 1
fi
trap - EXIT
echo 'Đã tạo warehouse/. Xem cách đặt biến môi trường và chạy ứng dụng trong warehouse/README.md.'
