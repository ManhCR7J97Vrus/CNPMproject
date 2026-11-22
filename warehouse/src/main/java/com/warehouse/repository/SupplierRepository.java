package com.warehouse.repository;

import com.warehouse.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng suppliers. */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
