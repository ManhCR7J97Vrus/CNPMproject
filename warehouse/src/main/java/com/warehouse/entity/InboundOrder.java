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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InboundStatus status;

    @Column(name = "supplier_notice", nullable = false)
    private boolean supplierNotice;

    @Column(name = "external_reference")
    private String externalReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_of_id")
    private OutboundOrder returnOf;

    @Column(name = "expected_at")
    private LocalDateTime expectedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
