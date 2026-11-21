package com.warehouse.repository;

import com.warehouse.entity.Stocktake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng stocktakes. */
@Repository
public interface StocktakeRepository extends JpaRepository<Stocktake, Long> {
}
