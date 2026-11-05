package com.bryan.expense.transaction;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, ObjectId> {

    List<Transaction> findByUserId(String userId);

    List<Transaction> findByUserIdAndType(String userId, TransactionType type);

    List<Transaction> findByUserIdAndDateBetween(
            String userId, LocalDateTime from, LocalDateTime to);

    List<Transaction> findByUserIdAndCategoryId(String userId, String categoryId);

    List<Transaction> findByUserIdAndAccountId(String userId, String accountId);
}