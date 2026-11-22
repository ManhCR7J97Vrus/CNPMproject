package com.warehouse.repository;

import com.warehouse.entity.Batch;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng batches. */
@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findByProductIdAndBatchNoIgnoreCase(Long productId, String batchNo);
}
