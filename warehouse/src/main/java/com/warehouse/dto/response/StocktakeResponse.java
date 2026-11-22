package com.warehouse.dto.response;

import com.warehouse.entity.enums.QuarantineStatus;
import com.warehouse.entity.enums.StocktakeStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StocktakeResponse(Long id, String referenceNo, StocktakeStatus status, String reason,
        LocalDateTime snapshotAt, String createdBy, Long counterId, String counter, String approvedBy,
        LocalDateTime approvedAt, List<Long> locationIds, List<Line> items) {

    public record Counter(Long id, String username, String fullName) {}

    public record Line(Long id, Long locationId, String locationCode, Long batchId, String batchNo, String skuCode,
                       BigDecimal expectedQty, BigDecimal countedQty, BigDecimal difference, String reason) {}

    public record Quarantine(Long id, String referenceNo, QuarantineStatus status, Long batchId, String batchNo,
            Long fromLocationId, String fromLocation, Long toLocationId, String toLocation, BigDecimal quantity,
            String reason, String createdBy, LocalDateTime createdAt, String approvedBy, LocalDateTime approvedAt,
            LocalDateTime completedAt) {}
}
