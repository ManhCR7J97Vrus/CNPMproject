package com.warehouse.entity;

import com.warehouse.entity.enums.ShipmentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ bảng shipments. */
@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    @Column(name = "carrier_name", nullable = false)
    private String carrierName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outbound_order_id", nullable = false)
    private OutboundOrder outboundOrder;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "driver_phone")
    private String driverPhone;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "driver_id", nullable = true)
    private User driver;

    @Column(name = "tracking_no")
    private String trackingNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
