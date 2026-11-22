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

    @Column(name = "perishable", nullable = false)
    private boolean perishable;

    @Column(name = "isolation_group")
    private String isolationGroup;

    @Column(name = "min_stock", nullable = false, precision = 18, scale = 3)
    private BigDecimal minStock;
}
