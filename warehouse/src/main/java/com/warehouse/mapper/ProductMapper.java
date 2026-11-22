package com.warehouse.mapper;

import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.Product;

public interface ProductMapper {
    static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getSkuCode(), product.getName(), product.getCategory(),
                product.getUnit(), product.isHasExpiry(), product.getExpiryAlertDays(), product.getMinStock(),
                product.getStorageCondition(), product.isActive(), product.isPerishable(), product.getIsolationGroup());
    }
}
