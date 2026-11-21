package com.warehouse.repository;

import com.warehouse.entity.InboundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inbound_items. */
@Repository
public interface InboundItemRepository extends JpaRepository<InboundItem, Long> {
}
