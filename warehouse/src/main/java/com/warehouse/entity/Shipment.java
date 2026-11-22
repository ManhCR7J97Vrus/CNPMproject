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
    @JoinColumn(name = "outbound_order_id", nullable = false, unique = true)
    private OutboundOrder outboundOrder;

    @Column(name = "driver_name")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;
    @Column(name = "dropoff_location", nullable = false)
    private String dropoffLocation;
    @Column(name = "contact_name")
    private String contactName;
    @Column(name = "contact_phone")
    private String contactPhone;
    @Column(name = "start_at")
    private LocalDateTime startAt;
    @Column(name = "end_at")
    private LocalDateTime endAt;
    @Column(name = "received_packages")
    private Integer receivedPackages;
    @Column(name = "delivery_note")
    private String deliveryNote;
    @Column(name = "return_requested", nullable = false)
    private boolean returnRequested;

    @Column(name = "checked_packages")
    private Integer checkedPackages;
    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
