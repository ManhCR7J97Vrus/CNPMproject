package com.warehouse.controller;

import com.warehouse.dto.request.ProductRequest;
import com.warehouse.dto.response.ProductResponse;
import com.warehouse.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import com.warehouse.dto.response.StorageProfileResponse;
import com.warehouse.entity.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Nhận yêu cầu HTTP cho phân hệ sản phẩm. */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping("/catalog")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SUPPLIER', 'STAFF', 'MANAGER')")
    public List<ProductResponse.CatalogItem> catalog() {
        return service.catalog();
    }

    @GetMapping("/storage-profiles")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public List<StorageProfileResponse> profiles() {
        return service.profiles();
    }

    @PutMapping("/storage-profiles/{code}")
    @PreAuthorize("hasRole('MANAGER')")
    public StorageProfileResponse saveProfile(@PathVariable String code, @Valid @RequestBody ProductRequest.Profile request) {
        return service.saveProfile(code, request);
    }

    @GetMapping("/priorities")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public Map<Priority, Integer> priorities() {
        return service.priorities();
    }

    @PutMapping("/priorities")
    @PreAuthorize("hasRole('MANAGER')")
    public Map<Priority, Integer> savePriorities(@Valid @RequestBody ProductRequest.Priorities request) {
        return service.savePriorities(request);
    }

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
