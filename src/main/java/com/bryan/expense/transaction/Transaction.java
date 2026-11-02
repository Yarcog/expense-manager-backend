package com.bryan.expense.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private ObjectId id;

    private String userId;
    private String categoryId;
    private TransactionType type;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private LocalDateTime createdAt;
}