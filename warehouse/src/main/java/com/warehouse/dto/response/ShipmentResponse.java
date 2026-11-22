package com.warehouse.dto.response;

import com.warehouse.entity.enums.ShipmentStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ShipmentResponse(Long id, Long outboundOrderId, String carrierName, String trackingNo,
        ShipmentStatus status, LocalDateTime dispatchedAt, LocalDateTime deliveredAt, String dispatchNo,
        Long driverId, String driverName, String driverPhone, Long vehicleId, String vehicleCode, String plateNumber,
        String pickupLocation, String dropoffLocation, String contactName, String contactPhone,
        LocalDateTime startAt, LocalDateTime endAt, Integer packageCount, Integer checkedPackages, LocalDateTime checkedAt,
        Integer receivedPackages, String deliveryNote, boolean returnRequested, List<OutboundOrderResponse.Event> history) {

    public record Driver(Long id, String username, String fullName, String phone, String carrierName, boolean available) {}
    public record Vehicle(Long id, String code, String plateNumber, String carrierName, boolean active) {}
}
