package com.bryan.expense.admin;

import com.bryan.expense.admin.dto.AdminStatsResponse;
import com.bryan.expense.admin.dto.UpdateRoleRequest;
import com.bryan.expense.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService service;

    @GetMapping("/users")
    public List<UserResponse> listAllUsers() {
        return service.listAllUsers();
    }

    @PutMapping("/users/{id}/role")
    public UserResponse updateUserRole(@PathVariable String id,
                                       @Valid @RequestBody UpdateRoleRequest request) {
        return service.updateUserRole(id, request.role());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public AdminStatsResponse getStats() {
        return service.getStats();
    }
}