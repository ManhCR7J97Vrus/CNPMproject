package com.warehouse.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.warehouse.entity.enums.OutboundStatus;
import com.warehouse.entity.enums.Priority;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OutboundOrderResponse(Long id, String orderNo, Priority priority, OutboundStatus status,
        String shippingAddress, LocalDateTime deliveryDeadline, List<Line> items, LocalDateTime createdAt,
        String receiverName, String receiverPhone, Integer packageCount, String dispatchNo,
        String customerName, List<Allocation> allocations, List<Event> history, String cancelRequestReason,
        LocalDateTime cancelRequestedAt, String cancelReason, Long shipmentId, LocalDateTime issuedAt) {

    public record Line(Long id, Long productId, BigDecimal qtyRequested, BigDecimal qtyPicked,
                       String skuCode, String productName, String storageCondition) {}

    public record Allocation(Long itemId, Long inventoryId, Long productId, String skuCode, Long batchId,
            String batchNo, Long locationId, String location, BigDecimal qtyRequested, BigDecimal qtyPicked) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Event(String type, LocalDateTime at, String note, String actor,
                        LocalDateTime previousAppointment, LocalDateTime appointment) {}

    public record Plan(List<Allocation> allocations, List<Shortage> shortages) {}

    public record Shortage(Long productId, String skuCode, BigDecimal missing) {}
}
