package com.bryan.expense.transaction;

import com.bryan.expense.category.Category;
import com.bryan.expense.category.CategoryRepository;
import com.bryan.expense.transaction.dto.TransactionRequest;
import com.bryan.expense.transaction.dto.TransactionResponse;
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
public class TransactionService {

    private final TransactionRepository repository;
    private final CategoryRepository categoryRepository;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return (String) auth.getPrincipal();
    }

    private void validateCategory(String categoryId, TransactionType type) {
        if (!ObjectId.isValid(categoryId)) {
            throw new IllegalArgumentException("La categoría no existe");
        }

        Category category = categoryRepository.findById(new ObjectId(categoryId))
                .orElseThrow(() -> new IllegalArgumentException("La categoría no existe"));

        if (!getCurrentUserId().equals(category.getUserId())) {
            throw new IllegalArgumentException("La categoría no existe");
        }

        if (!type.name().equals(category.getType())) {
            throw new IllegalArgumentException("El tipo de la transacción no coincide con el tipo de la categoría");
        }
    }

    public List<TransactionResponse> findAll() {
        return repository.findByUserId(getCurrentUserId())
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    public TransactionResponse findById(String id) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Transacción no encontrada");
        }

        Transaction t = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Transacción no encontrada"));

        if (!t.getUserId().equals(getCurrentUserId())) {
            throw new NoSuchElementException("Transacción no encontrada");
        }

        return TransactionResponse.from(t);
    }

    public List<TransactionResponse> filter(TransactionType type,
                                            LocalDateTime from,
                                            LocalDateTime to) {
        String userId = getCurrentUserId();
        List<Transaction> result;

        if (type != null && from != null && to != null) {
            result = repository.findByUserIdAndDateBetween(userId, from, to)
                    .stream()
                    .filter(t -> t.getType() == type)
                    .toList();
        } else if (type != null) {
            result = repository.findByUserIdAndType(userId, type);
        } else if (from != null && to != null) {
            result = repository.findByUserIdAndDateBetween(userId, from, to);
        } else {
            result = repository.findByUserId(userId);
        }

        return result.stream().map(TransactionResponse::from).toList();
    }

    public TransactionResponse create(TransactionRequest request) {
        validateCategory(request.categoryId(), request.type());

        Transaction transaction = Transaction.builder()
                .userId(getCurrentUserId())
                .categoryId(request.categoryId())
                .type(request.type())
                .amount(request.amount())
                .description(request.description())
                .date(request.date())
                .createdAt(LocalDateTime.now())
                .build();

        Transaction saved = repository.save(transaction);
        return TransactionResponse.from(saved);
    }

    public TransactionResponse update(String id, TransactionRequest request) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Transacción no encontrada");
        }

        Transaction t = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Transacción no encontrada"));

        if (!t.getUserId().equals(getCurrentUserId())) {
            throw new NoSuchElementException("Transacción no encontrada");
        }

        validateCategory(request.categoryId(), request.type());

        t.setCategoryId(request.categoryId());
        t.setType(request.type());
        t.setAmount(request.amount());
        t.setDescription(request.description());
        t.setDate(request.date());

        Transaction updated = repository.save(t);
        return TransactionResponse.from(updated);
    }

    public void delete(String id) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Transacción no encontrada");
        }

        Transaction t = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Transacción no encontrada"));

        if (!t.getUserId().equals(getCurrentUserId())) {
            throw new NoSuchElementException("Transacción no encontrada");
        }

        repository.deleteById(new ObjectId(id));
    }
}