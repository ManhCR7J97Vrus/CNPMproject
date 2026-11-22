package com.warehouse.repository;

import com.warehouse.entity.SupportTicket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByCustomerIdOrderByCreatedAtDescIdDesc(Long customerId);
}
