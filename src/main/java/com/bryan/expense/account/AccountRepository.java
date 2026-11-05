package com.bryan.expense.account;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends MongoRepository<Account, ObjectId> {

    List<Account> findByUserId(String userId);

    boolean existsByUserIdAndName(String userId, String name);
}
