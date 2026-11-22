package com.warehouse.service.impl;

import com.warehouse.dto.request.UserRequest;
import com.warehouse.dto.response.UserResponse;
import com.warehouse.entity.User;
import com.warehouse.entity.enums.Role;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.UserMapper;
import com.warehouse.repository.SupplierRepository;
import com.warehouse.repository.UserRepository;
import com.warehouse.service.UserService;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository users;
    private final SupplierRepository suppliers;
    private final PasswordEncoder passwords;
    private final Clock clock;
    private final AccountActor actor;
    private final StockWriteLock lock;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return users.findAll(Sort.by("username")).stream().map(UserMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return UserMapper.toResponse(find(id));
    }

    @Override
    public UserResponse createUser(UserRequest.Create request) {
        lock.acquire();
        String username = text(request.username(), "username");
        if (users.findByUsernameIgnoreCase(username).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATE, "username", "Tên đăng nhập đã dùng");
        }
        require(request.password().getBytes(StandardCharsets.UTF_8).length <= 72,
                "password", "Mật khẩu dài tối đa 72 byte UTF-8");
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwords.encode(request.password()));
        user.setCreatedAt(LocalDateTime.now(clock));
        apply(user, request.fullName(), request.email(), request.phone(), request.addr(), request.role(), request.supplierId(), request.carrierName());
        user.setActive(true);
        return UserMapper.toResponse(users.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest.Update request) {
        lock.acquire();
        User user = find(id);
        state(!actor.require().getId().equals(id) || (request.active() && request.role() == Role.ADMIN),
                "Không thể tự khóa hoặc hạ quyền tài khoản quản trị đang đăng nhập");
        apply(user, request.fullName(), request.email(), request.phone(), request.addr(), request.role(), request.supplierId(), request.carrierName());
        user.setActive(request.active());
        return UserMapper.toResponse(user);
    }

    private void apply(User user, String name, String email, String phone, String address, Role role, Long supplierId, String carrierName) {
        require((role == Role.SUPPLIER) == (supplierId != null), "supplierId",
                "Chọn nhà cung cấp cho tài khoản SUPPLIER");
        user.setFullName(text(name, "fullName"));
        user.setEmail(optional(email));
        user.setPhone(optional(phone));
        user.setAddr(optional(address));
        user.setRole(role);
        String carrier = optional(carrierName);
        require(role != Role.DISPATCHER || carrier != null, "carrierName", "Chọn đơn vị vận chuyển cho điều phối viên");
        require(carrier == null || role == Role.DISPATCHER || role == Role.DRIVER, "carrierName",
                "Chỉ tài xế và điều phối viên có đơn vị vận chuyển");
        user.setCarrierName(carrier == null ? null : code(carrier, "carrierName"));
        user.setSupplier(supplierId == null ? null : suppliers.findById(supplierId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy nhà cung cấp")));
    }

    private User find(Long id) {
        return users.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy tài khoản"));
    }
}
