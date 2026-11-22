package com.warehouse.repository;

import com.warehouse.entity.Inventory;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inventory. */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsByLocationId(Long locationId);
    List<Inventory> findByQtyOnHandGreaterThan(BigDecimal quantity);
    List<Inventory> findByLocationIdInOrderById(java.util.Collection<Long> locationIds);
    Optional<Inventory> findByBatchIdAndLocationId(Long batchId, Long locationId);
    List<Inventory> findByBatchProductIdAndQtyOnHandGreaterThan(Long productId, BigDecimal threshold);
    List<Inventory> findByLocationWarehouseNameAndLocationZoneNameAndLocationQuarantineFalseAndQtyOnHandGreaterThan(
            String warehouseName, String zoneName, BigDecimal threshold);

    /** Khóa dòng tồn trong transaction duyệt đơn và giữ chỗ. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.id = :id")
    Optional<Inventory> findByIdForUpdate(@Param("id") Long id);
}
