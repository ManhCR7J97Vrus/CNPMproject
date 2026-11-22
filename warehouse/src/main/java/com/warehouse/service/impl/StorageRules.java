package com.warehouse.service.impl;

import com.warehouse.entity.Batch;
import com.warehouse.entity.Location;
import com.warehouse.entity.Product;
import com.warehouse.entity.StorageProfile;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.repository.InventoryRepository;
import com.warehouse.repository.StorageProfileRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static com.warehouse.util.WarehouseRules.*;

@Component
@RequiredArgsConstructor
class StorageRules {
    private final StorageProfileRepository profiles;
    private final InventoryRepository inventory;

    String profileCode(String input) {
        if (optional(input) == null) {
            return null;
        }
        return profiles.findById(code(input, "storageCondition")).map(StorageProfile::getCode)
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_ERROR, "storageCondition", "Hồ sơ bảo quản không tồn tại"));
    }

    String condition(Batch batch) {
        return batch.getStorageCondition() == null ? batch.getProduct().getStorageCondition() : batch.getStorageCondition();
    }

    boolean accepts(Product product, String requiredProfile, Location location, boolean quarantine) {
        if (!location.isActive() || location.isLocked() || location.isQuarantine() != quarantine) {
            return false;
        }
        if (quarantine) {
            return true;
        }
        if (requiredProfile != null && !requiredProfile.equals(location.getStorageCondition())) {
            return false;
        }
        if (requiredProfile == null && product.getIsolationGroup() == null) {
            return true;
        }
        return inventory.findByLocationWarehouseNameAndLocationZoneNameAndLocationQuarantineFalseAndQtyOnHandGreaterThan(
                        location.getWarehouseName(), location.getZoneName(), BigDecimal.ZERO).stream()
                .allMatch(row -> compatible(product, requiredProfile, row.getBatch()));
    }

    boolean compatible(Product incoming, String profile, Batch existing) {
        Product other = existing.getProduct();
        if (other.getId().equals(incoming.getId())) {
            return true;
        }
        return !blocks(profile, other.getIsolationGroup())
                && !blocks(condition(existing), incoming.getIsolationGroup());
    }

    private boolean blocks(String profile, String group) {
        if (profile == null || group == null) {
            return false;
        }
        return profiles.findById(profile).map(StorageProfile::getIncompatibleGroups)
                .map(groups -> List.of(groups.split(",")).contains(group)).orElse(false);
    }
}
