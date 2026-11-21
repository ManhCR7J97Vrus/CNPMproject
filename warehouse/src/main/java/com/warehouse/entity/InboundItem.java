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
