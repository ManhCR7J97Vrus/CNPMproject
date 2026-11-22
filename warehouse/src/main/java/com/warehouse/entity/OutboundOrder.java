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

    @Column(name = "receiver_name")
    private String receiverName;
    @Column(name = "receiver_phone")
    private String receiverPhone;
    @Column(name = "package_count")
    private Integer packageCount;
    @Column(name = "dispatch_no", unique = true)
    private String dispatchNo;
    @Column(name = "issued_at")
    private LocalDateTime issuedAt;
    @Column(name = "cancel_reason")
    private String cancelReason;
    @Column(name = "cancel_request_reason")
    private String cancelRequestReason;
    @Column(name = "cancel_requested_at")
    private LocalDateTime cancelRequestedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
