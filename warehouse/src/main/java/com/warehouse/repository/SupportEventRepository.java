package com.warehouse.repository;

import com.warehouse.entity.SupportEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportEventRepository extends JpaRepository<SupportEvent, Long> {
    List<SupportEvent> findByTicketIdOrderByCreatedAtAscIdAsc(Long ticketId);
}
