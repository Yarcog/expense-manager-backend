package com.bryan.expense.user;

import com.bryan.expense.user.dto.ChangePasswordRequest;
import com.bryan.expense.user.dto.UpdateProfileRequest;
import com.bryan.expense.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/me")
    public UserResponse getMyProfile() {
        return service.getMyProfile();
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return service.updateMyProfile(request);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.changeMyPassword(request);
        return ResponseEntity.noContent().build();
    }
}
