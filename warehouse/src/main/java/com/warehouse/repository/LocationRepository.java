package com.warehouse.repository;

import com.warehouse.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng locations. */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
