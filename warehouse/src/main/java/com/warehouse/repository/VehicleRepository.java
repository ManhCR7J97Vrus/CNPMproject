package com.warehouse.repository;

import com.warehouse.entity.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCarrierNameIgnoreCaseOrderByCode(String carrier);
    boolean existsByCarrierNameIgnoreCaseAndCodeIgnoreCaseAndIdNot(String carrier, String code, Long id);
}
