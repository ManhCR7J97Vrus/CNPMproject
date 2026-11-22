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

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
