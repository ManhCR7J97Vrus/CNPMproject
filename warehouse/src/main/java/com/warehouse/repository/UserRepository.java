package com.warehouse.repository;

import com.warehouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng users. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
