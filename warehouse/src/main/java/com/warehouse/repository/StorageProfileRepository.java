package com.warehouse.repository;

import com.warehouse.entity.StorageProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageProfileRepository extends JpaRepository<StorageProfile, String> {}
