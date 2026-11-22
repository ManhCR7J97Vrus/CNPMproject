package com.warehouse.service.impl;

import com.warehouse.entity.*;
import com.warehouse.entity.enums.BatchStatus;
import com.warehouse.entity.enums.MovementType;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.repository.InventoryMovementRepository;
import com.warehouse.repository.InventoryRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static com.warehouse.util.WarehouseRules.*;

@Component
@RequiredArgsConstructor
class StockLedger {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final InventoryRepository inventory;
    private final InventoryMovementRepository movements;
    private final StorageRules storage;
    private final Clock clock;

    LocalDate today() {
        return LocalDate.now(clock);
    }

    LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    BigDecimal unreserved(Inventory row) {
        return row.getQtyOnHand().subtract(row.getQtyReserved());
    }

    BigDecimal available(Inventory row) {
        return eligible(row) ? unreserved(row) : ZERO;
    }

    boolean eligible(Inventory row) {
        Batch batch = row.getBatch();
        Location location = row.getLocation();
        return batch.getStatus() == BatchStatus.AVAILABLE
                && location.isActive() && !location.isLocked() && !location.isQuarantine()
                && (!batch.getProduct().isHasExpiry()
                    || (batch.getExpiryDate() != null && !batch.getExpiryDate().isBefore(today())));
    }

    Inventory rowForUpdate(Long id) {
        return inventory.findByIdForUpdate(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy dòng tồn"));
    }

    Inventory existing(Batch batch, Location location) {
        return inventory.findByBatchIdAndLocationId(batch.getId(), location.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không có tồn của lô tại ô này"));
    }

    void reserve(Inventory row, BigDecimal qty) {
        if (qty.compareTo(available(row)) > 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        row.setQtyReserved(row.getQtyReserved().add(qty));
        row.setUpdatedAt(now());
    }

    void release(Inventory row, BigDecimal qty) {
        state(row.getQtyReserved().compareTo(qty) >= 0, "Số giữ của dòng tồn không còn khớp");
        row.setQtyReserved(row.getQtyReserved().subtract(qty));
        row.setUpdatedAt(now());
    }

    void putAway(Batch batch, Location location, boolean quarantine, BigDecimal qty,
            MovementType type, String reference, User user, String reason) {
        checkDestination(batch, location, quarantine);
        if (batch.getReceivedAt() == null) {
            batch.setReceivedAt(now());
        }
        add(balance(batch, location), qty);
        append(batch, null, location, type, reference, qty, user, reason);
    }

    void dispatch(Inventory row, BigDecimal qty, String reference, User user) {
        release(row, qty);
        add(row, qty.negate());
        append(row.getBatch(), row.getLocation(), null, MovementType.OUTBOUND, reference, qty.negate(), user, null);
    }

    void adjust(Inventory row, BigDecimal counted, String reference, User user, String reason) {
        BigDecimal delta = counted.subtract(row.getQtyOnHand());
        if (delta.signum() == 0) {
            return;
        }
        add(row, delta);
        Location from = delta.signum() < 0 ? row.getLocation() : null;
        Location to = delta.signum() > 0 ? row.getLocation() : null;
        append(row.getBatch(), from, to, MovementType.ADJUSTMENT, reference, delta, user, reason);
    }

    void move(Inventory from, Location to, BigDecimal qty, boolean quarantine,
            String reference, User user, String reason) {
        state(!from.getLocation().getId().equals(to.getId()), "Ô nguồn và ô đích phải khác nhau");
        state(from.getLocation().isActive() && !from.getLocation().isLocked() && !from.getLocation().isQuarantine(),
                "Ô nguồn không còn được sử dụng để chuyển hàng");
        BigDecimal limit = quarantine ? unreserved(from) : available(from);
        if (qty.compareTo(limit) > 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, "quantity",
                    "Số có thể chuyển tối đa là " + limit.stripTrailingZeros().toPlainString());
        }
        Batch batch = from.getBatch();
        checkDestination(batch, to, quarantine);
        add(from, qty.negate());
        add(balance(batch, to), qty);
        append(batch, from.getLocation(), to, quarantine ? MovementType.QUARANTINE : MovementType.TRANSFER,
                reference, qty, user, reason);
    }

    void checkDestination(Batch batch, Location location, boolean quarantine) {
        state(storage.accepts(batch.getProduct(), storage.condition(batch), location, quarantine),
                "Ô " + location.getCode() + " không nhận hàng theo điều kiện hiện tại");
    }

    private Inventory balance(Batch batch, Location location) {
        return inventory.findByBatchIdAndLocationId(batch.getId(), location.getId()).orElseGet(() -> {
            Inventory row = new Inventory();
            row.setBatch(batch);
            row.setLocation(location);
            row.setQtyOnHand(ZERO);
            row.setQtyReserved(ZERO);
            row.setUpdatedAt(now());
            return inventory.save(row);
        });
    }

    private void add(Inventory row, BigDecimal delta) {
        BigDecimal total = row.getQtyOnHand().add(delta);
        state(total.compareTo(row.getQtyReserved()) >= 0, "Tồn mới thấp hơn số đang giữ");
        amount(total, "qtyOnHand");
        row.setQtyOnHand(total);
        row.setUpdatedAt(now());
        row.setStockRevision(row.getStockRevision() + 1);
    }

    private void append(Batch batch, Location from, Location to, MovementType type,
            String reference, BigDecimal qty, User user, String reason) {
        movements.append(batch.getId(), from == null ? null : from.getId(), to == null ? null : to.getId(),
                type, reference, qty, user.getId(), now(), reason);
    }
}
