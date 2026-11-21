package com.warehouse.repository;

import com.warehouse.entity.Inventory;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inventory. */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /** Khóa dòng tồn trong transaction duyệt đơn và giữ chỗ. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.id = :id")
    Optional<Inventory> findByIdForUpdate(@Param("id") Long id);
}
