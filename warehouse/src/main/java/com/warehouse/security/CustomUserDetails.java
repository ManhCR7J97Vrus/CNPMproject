package com.warehouse.security;

import com.warehouse.entity.enums.Role;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Biểu diễn danh tính và vai trò của người dùng đã xác thực. */
public class CustomUserDetails extends User {
    public CustomUserDetails(String username, Role role) {
        super(username, "", List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
    }
}
