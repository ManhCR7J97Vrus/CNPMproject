package com.warehouse.repository;

import com.warehouse.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng batches. */
@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
}
