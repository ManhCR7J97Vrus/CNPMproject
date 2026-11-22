package com.warehouse.service.impl;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.dto.response.StorageProfileResponse;
import com.warehouse.entity.Product;
import com.warehouse.entity.ServiceLevel;
import com.warehouse.entity.StorageProfile;
import com.warehouse.entity.enums.Priority;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.ProductMapper;
import com.warehouse.repository.ProductRepository;
import com.warehouse.repository.ServiceLevelRepository;
import com.warehouse.repository.StorageProfileRepository;
import com.warehouse.service.ProductService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository products;
    private final StorageProfileRepository storageProfiles;
    private final ServiceLevelRepository serviceLevels;
    private final StockWriteLock lock;
    private final StorageRules storage;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        return products.findAll(Sort.by("skuCode")).stream().map(ProductMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse.CatalogItem> catalog() {
        return products.findAll(Sort.by("skuCode")).stream().filter(Product::isActive)
                .map(p -> new ProductResponse.CatalogItem(p.getId(), p.getSkuCode(), p.getName(), p.getUnit(), p.getStorageCondition())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return ProductMapper.toResponse(find(id));
    }

    @Override
    public ProductResponse createProduct(ProductRequest.Write request) {
        lock.acquire();
        return write(new Product(), request);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest.Write request) {
        lock.acquire();
        return write(find(id), request);
    }

    private ProductResponse write(Product product, ProductRequest.Write request) {
        String sku = code(request.skuCode(), "skuCode");
        if (products.existsBySkuCodeIgnoreCaseAndIdNot(sku, product.getId() == null ? 0L : product.getId())) {
            throw new AppException(ErrorCode.DUPLICATE, "skuCode", "Mã SKU đã được sử dụng");
        }
        String profile = storage.profileCode(request.storageCondition());
        product.setSkuCode(sku);
        product.setName(text(request.name(), "name"));
        product.setCategory(text(request.category(), "category"));
        product.setUnit(text(request.unit(), "unit"));
        product.setHasExpiry(request.hasExpiry());
        product.setExpiryAlertDays(request.expiryAlertDays());
        product.setMinStock(amount(request.minStock(), "minStock"));
        product.setStorageCondition(profile);
        product.setActive(request.active());
        product.setPerishable(request.perishable());
        String group = optional(request.isolationGroup());
        product.setIsolationGroup(group == null ? null : group.toUpperCase(Locale.ROOT));
        return ProductMapper.toResponse(products.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageProfileResponse> profiles() {
        return storageProfiles.findAll(Sort.by("code")).stream().map(ProductServiceImpl::profileResponse).toList();
    }

    @Override
    public StorageProfileResponse saveProfile(String rawCode, ProductRequest.Profile request) {
        lock.acquire();
        String code = code(rawCode, "code");
        require(code.length() <= 255, "code", "Mã hồ sơ dài tối đa 255 ký tự");
        validateRange(request.temperatureMin(), request.temperatureMax(), "temperatureMin");
        validateRange(request.humidityMin(), request.humidityMax(), "humidityMin");
        List<String> groups = request.incompatibleGroups() == null ? List.of() : request.incompatibleGroups().stream()
                .map(g -> code(g, "incompatibleGroups")).distinct().sorted().toList();
        String storedGroups = String.join(",", groups);
        require(storedGroups.length() <= 255, "incompatibleGroups", "Danh sách nhóm quá dài");
        StorageProfile profile = storageProfiles.findById(code).orElseGet(() -> {
            StorageProfile created = new StorageProfile();
            created.setCode(code);
            return created;
        });
        profile.setTemperatureMin(request.temperatureMin());
        profile.setTemperatureMax(request.temperatureMax());
        profile.setHumidityMin(request.humidityMin());
        profile.setHumidityMax(request.humidityMax());
        profile.setAvoidLight(request.avoidLight());
        profile.setFireProtection(request.fireProtection());
        profile.setIncompatibleGroups(storedGroups);
        return profileResponse(storageProfiles.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Priority, Integer> priorities() {
        Map<Priority, Integer> result = new EnumMap<>(Priority.class);
        serviceLevels.findAll().forEach(level -> result.put(level.getPriority(), level.getRank()));
        return result;
    }

    @Override
    public Map<Priority, Integer> savePriorities(ProductRequest.Priorities request) {
        lock.acquire();
        Map<Priority, Integer> ranks = request.ranks();
        require(ranks.keySet().containsAll(Arrays.asList(Priority.values()))
                && new HashSet<>(ranks.values()).size() == Priority.values().length,
                "ranks", "Cần thứ hạng khác nhau cho cả ba dịch vụ");
        serviceLevels.findAll().forEach(level -> level.setRank(ranks.get(level.getPriority())));
        return ranks;
    }

    static StorageProfileResponse profileResponse(StorageProfile profile) {
        List<String> groups = profile.getIncompatibleGroups().isEmpty()
                ? List.of() : List.of(profile.getIncompatibleGroups().split(","));
        return new StorageProfileResponse(profile.getCode(), profile.getTemperatureMin(), profile.getTemperatureMax(),
                profile.getHumidityMin(), profile.getHumidityMax(), profile.isAvoidLight(), profile.isFireProtection(), groups);
    }

    private static void validateRange(BigDecimal lower, BigDecimal upper, String field) {
        require((lower == null) == (upper == null), field, "Cần nhập cả cận dưới và cận trên");
        require(lower == null || lower.compareTo(upper) <= 0, field, "Cận dưới không được vượt cận trên");
    }

    private Product find(Long id) {
        return products.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy SKU"));
    }
}
