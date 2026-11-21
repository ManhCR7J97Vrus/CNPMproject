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
