package com.warehouse.mapper;

import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.entity.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface OutboundOrderMapper {
    static OutboundOrderResponse toResponse(OutboundOrder order, List<OutboundItem> lines, boolean customer,
                                            List<OutboundOrderResponse.Event> history, Long shipmentId) {
        List<OutboundOrderResponse.Allocation> allocations = customer ? null : lines.stream()
                .filter(line -> line.getInventory() != null)
                .map(line -> allocation(line.getId(), line.getInventory(), line.getQtyRequested(), line.getQtyPicked())).toList();
        Map<Long, OutboundOrderResponse.Line> demands = new LinkedHashMap<>();
        for (OutboundItem line : lines) {
            Product p = line.getProduct();
            var item = new OutboundOrderResponse.Line(line.getId(), p.getId(), line.getQtyRequested(), line.getQtyPicked(),
                    p.getSkuCode(), p.getName(), line.getStorageCondition());
            demands.merge(p.getId(), item, (a, b) -> new OutboundOrderResponse.Line(a.id(), p.getId(),
                    a.qtyRequested().add(b.qtyRequested()), a.qtyPicked().add(b.qtyPicked()), p.getSkuCode(), p.getName(), a.storageCondition()));
        }
        return new OutboundOrderResponse(order.getId(), order.getOrderNo(), order.getPriority(), order.getStatus(),
                order.getShippingAddress(), order.getDeliveryDeadline(), List.copyOf(demands.values()), order.getCreatedAt(),
                order.getReceiverName(), order.getReceiverPhone(), order.getPackageCount(), customer ? null : order.getDispatchNo(),
                customer ? null : order.getCustomer().getFullName(), allocations, history, order.getCancelRequestReason(),
                order.getCancelRequestedAt(), order.getCancelReason(), customer ? null : shipmentId,
                customer ? null : order.getIssuedAt());
    }

    static OutboundOrderResponse.Allocation allocation(Long itemId, Inventory row, BigDecimal requested, BigDecimal picked) {
        Batch batch = row.getBatch();
        Location location = row.getLocation();
        return new OutboundOrderResponse.Allocation(itemId, row.getId(), batch.getProduct().getId(),
                batch.getProduct().getSkuCode(), batch.getId(), batch.getBatchNo(), location.getId(),
                location.getWarehouseName() + " / " + location.getCode(), requested, picked);
    }
}
