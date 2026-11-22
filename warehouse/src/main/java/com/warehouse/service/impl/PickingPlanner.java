package com.warehouse.service.impl;

import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.entity.*;
import com.warehouse.mapper.OutboundOrderMapper;
import com.warehouse.repository.InventoryRepository;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PickingPlanner {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final InventoryRepository inventory;
    private final StockLedger ledger;
    private final StorageRules storage;

    record Reservation(Long itemId, Inventory row, BigDecimal quantity) {}

    record Plan(List<Reservation> reservations, List<OutboundOrderResponse.Shortage> shortages) {
        OutboundOrderResponse.Plan response() {
            var allocations = reservations.stream()
                    .map(r -> OutboundOrderMapper.allocation(r.itemId(), r.row(), r.quantity(), ZERO)).toList();
            return new OutboundOrderResponse.Plan(allocations, shortages);
        }
    }

    Plan plan(List<OutboundItem> demands) {
        Map<Long, BigDecimal> allocated = new HashMap<>();
        Map<Long, List<Inventory>> choices = new HashMap<>();
        List<Reservation> reservations = new ArrayList<>();
        List<OutboundOrderResponse.Shortage> shortages = new ArrayList<>();
        for (OutboundItem demand : demands) {
            Product product = demand.getProduct();
            List<Inventory> candidates = choices.computeIfAbsent(product.getId(), id -> candidates(product));
            BigDecimal remaining = demand.getQtyRequested();
            for (Inventory row : candidates) {
                if (remaining.signum() == 0) {
                    break;
                }
                String required = demand.getStorageCondition();
                if (required != null && (!required.equals(storage.condition(row.getBatch()))
                        || !required.equals(row.getLocation().getStorageCondition()))) {
                    continue;
                }
                BigDecimal available = ledger.available(row).subtract(allocated.getOrDefault(row.getId(), ZERO));
                if (available.signum() <= 0) {
                    continue;
                }
                BigDecimal quantity = remaining.min(available);
                reservations.add(new Reservation(demand.getId(), row, quantity));
                allocated.merge(row.getId(), quantity, BigDecimal::add);
                remaining = remaining.subtract(quantity);
            }
            if (remaining.signum() > 0) {
                shortages.add(new OutboundOrderResponse.Shortage(product.getId(), product.getSkuCode(), remaining));
            }
        }
        return new Plan(reservations, shortages);
    }

    private List<Inventory> candidates(Product product) {
        Comparator<Inventory> received = Comparator
                .comparing((Inventory row) -> row.getBatch().getReceivedAt(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(row -> row.getBatch().getBatchNo())
                .thenComparing(row -> row.getLocation().getCode())
                .thenComparing(Inventory::getId);
        Comparator<Inventory> sorted = product.isHasExpiry()
                ? Comparator.comparing((Inventory row) -> row.getBatch().getExpiryDate(),
                      Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(received)
                : received;
        return inventory.findByBatchProductIdAndQtyOnHandGreaterThan(product.getId(), ZERO).stream()
                .filter(row -> ledger.available(row).signum() > 0).sorted(sorted).toList();
    }
}
