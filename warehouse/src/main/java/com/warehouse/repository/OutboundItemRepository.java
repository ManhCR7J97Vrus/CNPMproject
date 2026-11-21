package com.warehouse.repository;

import com.warehouse.entity.OutboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng outbound_items. */
@Repository
public interface OutboundItemRepository extends JpaRepository<OutboundItem, Long> {
}
