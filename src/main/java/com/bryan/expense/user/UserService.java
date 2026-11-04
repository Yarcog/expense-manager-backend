package com.bryan.expense.user;

import com.bryan.expense.user.dto.ChangePasswordRequest;
import com.bryan.expense.user.dto.RegisterRequest;
import com.bryan.expense.user.dto.UpdateProfileRequest;
import com.bryan.expense.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

    public UserResponse getMyProfile() {
        return UserResponse.from(getCurrentUser());
    }

    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        user.setName(request.name());
        user.setCurrency(request.currency().toUpperCase());

        User updated = repository.save(user);
        return UserResponse.from(updated);
    }

    public void changeMyPassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        repository.save(user);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return (String) auth.getPrincipal();
    }

    private User getCurrentUser() {
        return repository.findById(new ObjectId(getCurrentUserId()))
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    private Role determineRole(String adminCode) {
        if (adminCode != null && adminCode.equals(adminSecretCode)) {
            return Role.ADMIN;
        }
        return Role.USER;
    }
}