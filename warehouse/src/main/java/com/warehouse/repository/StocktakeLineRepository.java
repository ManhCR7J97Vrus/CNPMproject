package com.warehouse.repository;

import com.warehouse.entity.StocktakeLine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocktakeLineRepository extends JpaRepository<StocktakeLine, Long> {
    List<StocktakeLine> findByStocktakeIdOrderById(Long stocktakeId);
}
