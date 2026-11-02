package com.bryan.expense.auth;

import com.bryan.expense.security.JwtService;
import com.bryan.expense.security.dto.AuthResponse;
import com.bryan.expense.security.dto.LoginRequest;
import com.bryan.expense.user.User;
import com.bryan.expense.user.UserRepository;
import com.bryan.expense.user.UserService;
import com.bryan.expense.user.dto.RegisterRequest;
import com.bryan.expense.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request);
        String token = jwtService.generateToken(user.id(), user.email(), user.role());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getId().toHexString(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }
}