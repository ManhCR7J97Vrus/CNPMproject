package com.warehouse.service.impl;

import com.warehouse.entity.User;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.repository.UserRepository;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AccountActor {
    private final UserRepository users;

    User require() {
        return require(SecurityContextHolder.getContext().getAuthentication());
    }

    User require(Principal principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return users.findByUsernameIgnoreCase(principal.getName())
                .filter(User::isActive).orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }
}
