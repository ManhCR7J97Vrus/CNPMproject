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
