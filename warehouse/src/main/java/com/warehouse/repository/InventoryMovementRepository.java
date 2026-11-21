package com.warehouse.repository;

import com.warehouse.entity.InventoryMovement;
import com.warehouse.entity.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Đọc và ghi thêm sổ kho, chỉ công bố hai thao tác. */
@Repository
public interface InventoryMovementRepository extends org.springframework.data.repository.Repository<InventoryMovement, Long> {
    Optional<InventoryMovement> findById(Long id);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO inventory_movements
                (batch_id, from_location_id, to_location_id, movement_type,
                 reference_no, change_qty, created_by, created_at)
            VALUES (:batchId, :fromId, :toId, :#{#type.name()},
                    :reference, :delta, :actorId, :at)
            """, nativeQuery = true)
    int append(@Param("batchId") Long batchId, @Param("fromId") Long fromId,
               @Param("toId") Long toId, @Param("type") MovementType type,
               @Param("reference") String reference, @Param("delta") BigDecimal delta,
               @Param("actorId") Long actorId, @Param("at") LocalDateTime at);
}
