package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng locations. */
@Entity
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_name", "code"}))
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Column(name = "storage_condition")
    private String storageCondition;

    @Column(name = "rack_code")
    private String rackCode;

    @Column(name = "is_quarantine", nullable = false)
    private boolean quarantine;

    @Column(name = "is_locked", nullable = false)
    private boolean locked;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
