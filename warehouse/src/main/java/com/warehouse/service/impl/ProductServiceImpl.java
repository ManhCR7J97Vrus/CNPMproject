package com.warehouse.service.impl;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ sản phẩm. */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public ProductResponse createProduct(ProductRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
