package com.warehouse.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class ShipmentRequest {
    private ShipmentRequest() {}

    public record Create(@NotNull @Positive Long outboundOrderId, @NotBlank @Size(max = 255) String carrierName,
                         @NotBlank @Size(max = 255) String pickupLocation, @Size(max = 255) String dropoffLocation,
                         @Size(max = 255) String contactName, @Size(max = 255) String contactPhone) {}

    public record Assign(@NotNull @Positive Long driverId, @NotNull @Positive Long vehicleId,
                         @NotNull LocalDateTime startAt, @NotNull LocalDateTime endAt,
                         @Size(max = 255) String trackingNo) {}

    public record Receipt(@Positive Integer packageCount, @Size(max = 255) String note) {}
    public record Delivery(@NotNull Boolean delivered, @Size(max = 255) String note, boolean requestReturn) {}
    public record ReturnRequest(@NotBlank @Size(max = 255) String reason) {}

    public enum ChangeKind { LATE_PICKUP, NO_SHOW, REDELIVERY }
    public record Reschedule(@NotNull ChangeKind kind, @NotNull LocalDateTime startAt, @NotNull LocalDateTime endAt,
                             @NotBlank @Size(max = 255) String reason) {}

    public record Driver(@NotBlank @Size(max = 255) String carrierName, @Size(max = 255) String phone, boolean available) {}
    public record Vehicle(@NotBlank @Size(max = 255) String carrierName, @NotBlank @Size(max = 255) String code,
                          @NotBlank @Size(max = 255) String plateNumber, boolean active) {}
}
