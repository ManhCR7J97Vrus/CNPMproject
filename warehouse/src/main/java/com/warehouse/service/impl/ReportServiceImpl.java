package com.warehouse.service.impl;

import com.warehouse.dto.request.ReportRequest;
import com.warehouse.dto.response.ReportResponse;
import com.warehouse.entity.InventoryMovement;
import com.warehouse.entity.Location;
import com.warehouse.entity.Product;
import com.warehouse.repository.InventoryMovementRepository;
import com.warehouse.service.ReportService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final InventoryMovementRepository movements;

    @Override
    public List<ReportResponse> stockSummary(ReportRequest.Period request) {
        require(!request.from().isAfter(request.to()), "from", "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        require(request.to().isBefore(LocalDate.of(9999, 12, 31)),
                "to", "Ngày kết thúc nằm ngoài phạm vi báo cáo");
        LocalDateTime start = request.from().atStartOfDay();
        LocalDateTime endExclusive = request.to().plusDays(1).atStartOfDay();
        String warehouse = optional(request.warehouseName());
        String sku = optional(request.sku());
        Map<Long, Totals> totals = new HashMap<>();
        for (InventoryMovement movement : movements.findByCreatedAtLessThanOrderByCreatedAtAscIdAsc(endExclusive)) {
            Product product = movement.getBatch().getProduct();
            if (sku != null && !sku.equalsIgnoreCase(product.getSkuCode())) {
                continue;
            }
            boolean from = inScope(movement.getFromLocation(), warehouse);
            boolean to = inScope(movement.getToLocation(), warehouse);
            if (!from && !to) {
                continue;
            }
            BigDecimal signed = movement.getChangeQty().abs()
                    .multiply(BigDecimal.valueOf((to ? 1 : 0) - (from ? 1 : 0)));
            Totals row = totals.computeIfAbsent(product.getId(), ignored -> new Totals(product));
            if (movement.getCreatedAt().isBefore(start)) {
                row.opening = row.opening.add(signed);
            } else {
                row.record(movement, signed);
            }
        }
        return totals.values().stream().map(Totals::response).sorted(Comparator.comparing(ReportResponse::skuCode)).toList();
    }

    private static boolean inScope(Location location, String warehouse) {
        return location != null && (warehouse == null || warehouse.equalsIgnoreCase(location.getWarehouseName()));
    }

    private static class Totals {
        private final Product product;
        private BigDecimal opening = BigDecimal.ZERO;
        private BigDecimal inbound = BigDecimal.ZERO;
        private BigDecimal outbound = BigDecimal.ZERO;
        private BigDecimal adjustment = BigDecimal.ZERO;

        Totals(Product product) {
            this.product = product;
        }

        void record(InventoryMovement movement, BigDecimal signed) {
            switch (movement.getMovementType()) {
                case INBOUND, RETURN -> inbound = inbound.add(signed);
                case OUTBOUND -> outbound = outbound.subtract(signed);
                case ADJUSTMENT, TRANSFER, QUARANTINE -> adjustment = adjustment.add(signed);
            }
        }

        ReportResponse response() {
            return new ReportResponse(product.getId(), product.getSkuCode(), opening, inbound, outbound, adjustment,
                    opening.add(inbound).subtract(outbound).add(adjustment));
        }
    }
}
