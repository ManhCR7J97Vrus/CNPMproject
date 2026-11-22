package com.warehouse.service.impl;

import com.warehouse.entity.Supplier;
import com.warehouse.entity.User;
import com.warehouse.entity.enums.Role;
import com.warehouse.repository.SupplierRepository;
import com.warehouse.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountBootstrap implements ApplicationRunner {
    private final UserRepository users;
    private final SupplierRepository suppliers;
    private final PasswordEncoder passwords;
    private final Clock clock;
    private final StockWriteLock lock;

    @Value("${WAREHOUSE_ADMIN_PASSWORD:}")
    private String password;
    @Value("${warehouse.demo:false}")
    private boolean demo;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (password.isBlank() && !demo) {
            return;
        }
        if (password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("WAREHOUSE_ADMIN_PASSWORD cần tối thiểu 8 ký tự, tối đa 72 byte");
        }
        lock.acquire();
        create("admin", "Quản trị hệ thống", Role.ADMIN, null);
        if (demo) {
            create("manager", "Quản lý kho", Role.MANAGER, null);
            create("staff", "Nhân viên kho", Role.STAFF, null);
            create("customer", "Khách hàng", Role.CUSTOMER, null);
            create("customer2", "Khách hàng 2", Role.CUSTOMER, null);
            create("driver", "Tài xế", Role.DRIVER, null);
            create("dispatcher", "Điều phối viên", Role.DISPATCHER, null);
            if (users.findByUsernameIgnoreCase("supplier").isEmpty()) {
                Supplier supplier = suppliers.findAll().stream().filter(s -> s.getCode().equals("NCC-DEMO"))
                        .findFirst().orElseGet(this::createDemoSupplier);
                create("supplier", "Nhà cung cấp mẫu", Role.SUPPLIER, supplier);
            }
        }
    }

    private Supplier createDemoSupplier() {
        Supplier supplier = new Supplier();
        supplier.setCode("NCC-DEMO");
        supplier.setName("Nhà cung cấp mẫu");
        supplier.setActive(true);
        return suppliers.save(supplier);
    }

    private void create(String username, String name, Role role, Supplier supplier) {
        if (users.findByUsernameIgnoreCase(username).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setFullName(name);
        user.setRole(role);
        if (role == Role.DRIVER || role == Role.DISPATCHER) {
            user.setCarrierName("VAN-CHUYEN");
        }
        user.setPasswordHash(passwords.encode(password));
        user.setActive(true);
        user.setSupplier(supplier);
        user.setCreatedAt(LocalDateTime.now(clock));
        users.save(user);
    }
}
