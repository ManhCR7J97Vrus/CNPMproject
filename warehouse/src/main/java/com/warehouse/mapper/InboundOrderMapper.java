package com.warehouse.mapper;

import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.entity.*;
import java.math.BigDecimal;
import java.util.List;

public interface InboundOrderMapper {
    static InboundOrderResponse toResponse(InboundOrder order, List<InboundItem> items, boolean supplierView) {
        Supplier supplier = order.getSupplier();
        return new InboundOrderResponse(order.getId(), order.getOrderNo(), supplier == null ? null : supplier.getId(),
                order.getStatus(), order.getExpectedAt(), items.stream().map(i -> line(i, supplierView)).toList(),
                order.getExternalReference(), supplierView ? null : order.getCreatedBy().getUsername(), order.getCreatedAt(),
                order.getReturnOf() == null ? null : order.getReturnOf().getId(), supplier == null ? null : supplier.getName());
    }

    private static InboundOrderResponse.Line line(InboundItem item, boolean supplierView) {
        Product product = item.getProduct();
        if (supplierView) {
            return new InboundOrderResponse.Line(item.getId(), product.getId(), null, null, item.getQtyOrdered(),
                    null, null, null, null, null, null, null, null, null, null, null,
                    product.getSkuCode(), product.getName(), null, null, null, null);
        }
        Location location = item.getLocation();
        return new InboundOrderResponse.Line(item.getId(), product.getId(), item.getBatch() == null ? null : item.getBatch().getId(),
                location == null ? null : location.getId(), item.getQtyOrdered(), item.getQtyReceived(), item.getUnitCost(),
                item.getQtyAccepted(), item.getQtyReceived().subtract(item.getQtyAccepted()),
                item.getQtyOrdered().subtract(item.getQtyReceived()).max(BigDecimal.ZERO), item.getProposedBatchNo(),
                location == null ? null : location.getCode(), item.getMfgDate(), item.getExpiryDate(), item.getItemCondition(),
                item.getDiscrepancyReason(), product.getSkuCode(), product.getName(), product.getCategory(),
                product.isPerishable(), item.getStorageCondition(), product.isHasExpiry());
    }
}
