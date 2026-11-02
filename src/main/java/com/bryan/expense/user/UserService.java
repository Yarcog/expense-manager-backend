package com.bryan.expense.user;

import com.bryan.expense.user.dto.RegisterRequest;
import com.bryan.expense.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.secret-code}")
    private String adminSecretCode;

    public UserResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Role role = determineRole(request.adminCode());

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .currency("COP")
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        User saved = repository.save(user);
        return UserResponse.from(saved);
    }

    private Role determineRole(String adminCode) {
        if (adminCode != null && adminCode.equals(adminSecretCode)) {
            return Role.ADMIN;
        }
        return Role.USER;
    }
}