package com.bryan.expense.account;

import com.bryan.expense.account.dto.AccountRequest;
import com.bryan.expense.account.dto.AccountResponse;
import com.bryan.expense.transaction.Transaction;
import com.bryan.expense.transaction.TransactionRepository;
import com.bryan.expense.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return (String) auth.getPrincipal();
    }

    private Account findOwnedAccount(String id, String userId) {
        if (!ObjectId.isValid(id)) {
            throw new NoSuchElementException("Cuenta no encontrada");
        }

        Account account = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new NoSuchElementException("Cuenta no encontrada"));

        if (!account.getUserId().equals(userId)) {
            throw new NoSuchElementException("Cuenta no encontrada");
        }

        return account;
    }

    private List<Transaction> findTransactions(Account account) {
        return transactionRepository.findByUserIdAndAccountId(
                account.getUserId(), account.getId().toHexString());
    }

    // El saldo no se guarda: siempre se calcula a partir de las transacciones de la cuenta.
    private AccountResponse toResponse(Account account, List<Transaction> transactions) {
        double balance = account.getInitialBalance();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                balance += t.getAmount();
            } else if (t.getType() == TransactionType.EXPENSE) {
                balance -= t.getAmount();
            }
        }

        return AccountResponse.from(account, balance);
    }

    public List<AccountResponse> findAll() {
        String userId = getCurrentUserId();

        // Una sola consulta de transacciones, en vez de una por cada cuenta.
        Map<String, List<Transaction>> byAccount = transactionRepository.findByUserId(userId)
                .stream()
                .filter(t -> t.getAccountId() != null)
                .collect(Collectors.groupingBy(Transaction::getAccountId));

        return repository.findByUserId(userId)
                .stream()
                .map(a -> toResponse(a, byAccount.getOrDefault(a.getId().toHexString(), List.of())))
                .toList();
    }

    public AccountResponse findById(String id) {
        Account account = findOwnedAccount(id, getCurrentUserId());
        return toResponse(account, findTransactions(account));
    }

    public AccountResponse create(AccountRequest request) {
        String userId = getCurrentUserId();

        if (repository.existsByUserIdAndName(userId, request.name())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese nombre");
        }

        Account account = Account.builder()
                .userId(userId)
                .name(request.name())
                .type(request.type())
                .initialBalance(request.initialBalance())
                .color(request.color())
                .createdAt(LocalDateTime.now())
                .build();

        Account saved = repository.save(account);
        return toResponse(saved, List.of());
    }

    public AccountResponse update(String id, AccountRequest request) {
        String userId = getCurrentUserId();
        Account account = findOwnedAccount(id, userId);

        if (!request.name().equals(account.getName())
                && repository.existsByUserIdAndName(userId, request.name())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese nombre");
        }

        account.setName(request.name());
        account.setType(request.type());
        account.setInitialBalance(request.initialBalance());
        account.setColor(request.color());

        Account updated = repository.save(account);
        return toResponse(updated, findTransactions(updated));
    }

    public void delete(String id) {
        Account account = findOwnedAccount(id, getCurrentUserId());

        if (!findTransactions(account).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la cuenta porque tiene transacciones asociadas");
        }

        repository.deleteById(account.getId());
    }
}
