package com.warehouse.service;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ sản phẩm. */
public interface ProductService {
    List<ProductResponse> listProducts();

    ProductResponse getProduct(Long id);

    ProductResponse createProduct(ProductRequest.Write request);

    ProductResponse updateProduct(Long id, ProductRequest.Write request);
}
