package com.warehouse.service.impl;

import com.warehouse.dto.request.LocationRequest;
import com.warehouse.dto.response.LocationResponse;
import com.warehouse.entity.Location;
import com.warehouse.entity.Product;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.LocationMapper;
import com.warehouse.repository.InventoryRepository;
import com.warehouse.repository.LocationRepository;
import com.warehouse.repository.ProductRepository;
import com.warehouse.service.LocationService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locations;
    private final InventoryRepository inventory;
    private final ProductRepository products;
    private final StorageRules storage;
    private final StockWriteLock lock;

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> listLocations() {
        return locations.findAll(Sort.by("warehouseName", "zoneName", "rackCode", "code")).stream()
                .map(LocationMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> suggestLocations(Long productId, String condition, boolean quarantine) {
        Product product = products.findById(productId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        String profile = optional(condition) == null ? product.getStorageCondition() : storage.profileCode(condition);
        return locations.findAll(Sort.by("warehouseName", "code")).stream()
                .filter(location -> storage.accepts(product, profile, location, quarantine))
                .map(LocationMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long id) {
        return LocationMapper.toResponse(find(id));
    }

    @Override
    public LocationResponse createLocation(LocationRequest.Write request) {
        lock.acquire();
        return write(new Location(), request);
    }

    @Override
    public LocationResponse updateLocation(Long id, LocationRequest.Write request) {
        lock.acquire();
        return write(find(id), request);
    }

    private LocationResponse write(Location location, LocationRequest.Write request) {
        String warehouse = code(request.warehouseName(), "warehouseName");
        String code = code(request.code(), "code");
        if (locations.existsByWarehouseNameIgnoreCaseAndCodeIgnoreCaseAndIdNot(warehouse, code,
                location.getId() == null ? 0L : location.getId())) {
            throw new AppException(ErrorCode.DUPLICATE, "code", "Mã ô đã dùng trong kho này");
        }
        String profile = storage.profileCode(request.storageCondition());
        String zone = text(request.zoneName(), "zoneName");
        if (location.getId() != null && (location.isQuarantine() != request.quarantine()
                || !warehouse.equals(location.getWarehouseName()) || !code.equals(location.getCode())
                || !zone.equals(location.getZoneName()) || !java.util.Objects.equals(profile, location.getStorageCondition()))) {
            state(!inventory.existsByLocationId(location.getId()), "Không đổi địa chỉ, hồ sơ hoặc loại ô đã có sổ tồn");
        }
        location.setCode(code);
        location.setWarehouseName(warehouse);
        location.setZoneName(zone);
        location.setRackCode(optional(request.rackCode()));
        location.setStorageCondition(profile);
        location.setActive(request.active());
        location.setLocked(request.locked());
        location.setQuarantine(request.quarantine());
        return LocationMapper.toResponse(locations.save(location));
    }

    private Location find(Long id) {
        return locations.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ô kho"));
    }
}
