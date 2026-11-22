package com.warehouse.repository;

import com.warehouse.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng products. */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySkuCodeIgnoreCaseAndIdNot(String skuCode, Long id);
}
