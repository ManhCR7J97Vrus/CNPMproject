package com.warehouse.controller;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ sản phẩm. */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<ProductResponse> listProducts() {
        return service.listProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public ProductResponse getProduct(@PathVariable("id") Long id) {
        return service.getProduct(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest.Write request) {
        return service.createProduct(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ProductResponse updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductRequest.Write request) {
        return service.updateProduct(id, request);
    }
}
