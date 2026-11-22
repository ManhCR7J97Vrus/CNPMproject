package com.warehouse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stocktake_lines", uniqueConstraints = @UniqueConstraint(columnNames = {"stocktake_id", "inventory_id"}))
@Getter
@Setter
@NoArgsConstructor
public class StocktakeLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stocktake_id", nullable = false)
    private Stocktake stocktake;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "expected_qty", nullable = false, precision = 18, scale = 3)
    private BigDecimal expectedQty;
    @Column(name = "counted_qty", precision = 18, scale = 3)
    private BigDecimal countedQty;
    @Column(name = "stock_revision", nullable = false)
    private long stockRevision;
    @Column(name = "reason")
    private String reason;
}
