package com.warehouse.repository;

import com.warehouse.entity.Shipment;
import com.warehouse.entity.enums.ShipmentStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng shipments. */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByOutboundOrderId(Long orderId);
    List<Shipment> findByDriverIdOrderByIdDesc(Long driverId);
    List<Shipment> findByCarrierNameIgnoreCaseOrderByIdDesc(String carrier);

    @Query("""
            select s from Shipment s
            where s.id <> :exceptId and s.status in :states and s.startAt < :end and s.endAt > :start
              and (s.driver.id = :driverId or s.vehicle.id = :vehicleId)
            """)
    List<Shipment> findScheduleConflicts(Long exceptId, Collection<ShipmentStatus> states,
            LocalDateTime start, LocalDateTime end, Long driverId, Long vehicleId);
}
