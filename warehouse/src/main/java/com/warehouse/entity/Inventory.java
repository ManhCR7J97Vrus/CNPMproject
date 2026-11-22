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

    @Column(name = "stock_revision", nullable = false)
    private long stockRevision;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
