package com.warehouse.service;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import java.util.List;
import java.util.Map;
import com.warehouse.entity.enums.Priority;
import com.warehouse.dto.response.StorageProfileResponse;

/** Hợp đồng nghiệp vụ cho phân hệ sản phẩm. */
public interface ProductService {
    List<ProductResponse.CatalogItem> catalog();
    List<StorageProfileResponse> profiles();
    StorageProfileResponse saveProfile(String code, ProductRequest.Profile request);
    Map<Priority, Integer> priorities();
    Map<Priority, Integer> savePriorities(ProductRequest.Priorities request);

    List<ProductResponse> listProducts();

    ProductResponse getProduct(Long id);

    ProductResponse createProduct(ProductRequest.Write request);

    ProductResponse updateProduct(Long id, ProductRequest.Write request);
}
