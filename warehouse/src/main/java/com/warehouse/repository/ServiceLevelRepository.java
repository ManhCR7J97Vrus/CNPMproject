package com.warehouse.repository;

import com.warehouse.entity.ServiceLevel;
import com.warehouse.entity.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceLevelRepository extends JpaRepository<ServiceLevel, Priority> {}
