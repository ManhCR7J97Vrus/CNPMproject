package com.warehouse.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.warehouse.entity.enums.InboundStatus;
import com.warehouse.entity.enums.ItemCondition;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InboundOrderResponse(Long id, String orderNo, Long supplierId, InboundStatus status,
        LocalDateTime expectedAt, List<Line> items, String externalReference, String createdBy,
        LocalDateTime createdAt, Long returnOfId, String supplierName) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Line(Long id, Long productId, Long batchId, Long locationId, BigDecimal qtyOrdered,
            BigDecimal qtyReceived, BigDecimal unitCost, BigDecimal qtyAccepted, BigDecimal qtyRejected,
            BigDecimal qtyMissing, String batchNo, String locationCode, LocalDate mfgDate, LocalDate expiryDate,
            ItemCondition itemCondition, String discrepancyReason, String skuCode, String productName,
            String category, Boolean perishable, String storageCondition, Boolean hasExpiry) {}
}
