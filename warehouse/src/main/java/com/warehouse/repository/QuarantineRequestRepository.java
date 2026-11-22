package com.warehouse.repository;

import com.warehouse.entity.QuarantineRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarantineRequestRepository extends JpaRepository<QuarantineRequest, Long> {}
