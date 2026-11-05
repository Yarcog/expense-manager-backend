package com.bryan.expense.account;

import com.bryan.expense.account.dto.AccountRequest;
import com.bryan.expense.account.dto.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public List<AccountResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        AccountResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable String id,
                                  @Valid @RequestBody AccountRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
