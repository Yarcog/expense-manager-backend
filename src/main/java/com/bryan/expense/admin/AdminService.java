package com.bryan.expense.admin;

import com.bryan.expense.admin.dto.AdminStatsResponse;
import com.bryan.expense.category.CategoryRepository;
import com.bryan.expense.transaction.TransactionRepository;
import com.bryan.expense.user.Role;
import com.bryan.expense.user.User;
import com.bryan.expense.user.UserRepository;
import com.bryan.expense.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public List<UserResponse> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse updateUserRole(String userId, Role newRole) {
        if (!ObjectId.isValid(userId)) {
            throw new NoSuchElementException("Usuario no encontrado");
        }

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        // Prevenir que un admin se quite a sí mismo el rol
        String currentUserId = getCurrentUserId();
        if (user.getId().toHexString().equals(currentUserId) && newRole != Role.ADMIN) {
            throw new IllegalArgumentException("No puedes quitarte el rol de ADMIN a ti mismo");
        }

        user.setRole(newRole);
        User updated = userRepository.save(user);
        return UserResponse.from(updated);
    }

    public void deleteUser(String userId) {
        if (!ObjectId.isValid(userId)) {
            throw new NoSuchElementException("Usuario no encontrado");
        }

        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        // Prevenir que un admin se elimine a sí mismo
        String currentUserId = getCurrentUserId();
        if (user.getId().toHexString().equals(currentUserId)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo");
        }

        // Cascade delete: borrar categorías y transacciones del usuario
        categoryRepository.deleteAll(categoryRepository.findByUserId(userId));
        transactionRepository.deleteAll(transactionRepository.findByUserId(userId));

        userRepository.deleteById(new ObjectId(userId));
    }

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();
        long totalCategories = categoryRepository.count();
        long totalTransactions = transactionRepository.count();

        return new AdminStatsResponse(totalUsers, totalAdmins, totalCategories, totalTransactions);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return (String) auth.getPrincipal();
    }
}