package com.bryan.expense.category;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, ObjectId> {

    List<Category> findByUserId(String userId);

    List<Category> findByUserIdAndType(String userId, String type);

    boolean existsByUserIdAndName(String userId, String name);
}