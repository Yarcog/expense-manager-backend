package com.bryan.expense.category;

import com.bryan.expense.category.dto.CategoryRequest;
import com.bryan.expense.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return (String) auth.getPrincipal();
    }

    public List<CategoryResponse> findAll() {
        return repository.findByUserId(getCurrentUserId())
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse findById(String id) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Categoría no encontrada");
        }

        Category category = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));

        if (!category.getUserId().equals(getCurrentUserId())) {
            throw new NoSuchElementException("Categoría no encontrada");
        }

        return CategoryResponse.from(category);
    }

    public CategoryResponse create(CategoryRequest request) {
        String userId = getCurrentUserId();

        if (repository.existsByUserIdAndName(userId, request.name())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Category category = Category.builder()
                .userId(userId)
                .name(request.name())
                .type(request.type())
                .icon(request.icon())
                .color(request.color())
                .createdAt(LocalDateTime.now())
                .build();

        Category saved = repository.save(category);
        return CategoryResponse.from(saved);
    }

    public CategoryResponse update(String id, CategoryRequest request) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Categoría no encontrada");
        }

        Category category = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));

        if (!category.getUserId().equals(getCurrentUserId())) {
            throw new NoSuchElementException("Categoría no encontrada");
        }

        category.setName(request.name());
        category.setType(request.type());
        category.setIcon(request.icon());
        category.setColor(request.color());

        Category updated = repository.save(category);
        return CategoryResponse.from(updated);
    }

    public void delete(String id) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Categoría no encontrada");
        }

        Category category = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));

        if (!category.getUserId().equals(getCurrentUserId())) {
            throw new NoSuchElementException("Categoría no encontrada");
        }

        repository.deleteById(new ObjectId(id));
    }
}