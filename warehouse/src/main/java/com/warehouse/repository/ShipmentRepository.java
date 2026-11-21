package com.warehouse.repository;

import com.warehouse.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng shipments. */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
