package com.bryan.expense.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${spring.data.mongodb.uri:NO_DEFINIDA}")
    private String uriOld;

    @Value("${spring.mongodb.uri:NO_DEFINIDA}")
    private String uriNew;

    @Value("${spring.data.mongodb.database:NO_DEFINIDA}")
    private String dbOld;

    @Value("${spring.mongodb.database:NO_DEFINIDA}")
    private String dbNew;

    @GetMapping("/mongodb")
    public Map<String, String> verConfig() {
        return Map.of(
                "spring.data.mongodb.uri", uriOld,
                "spring.mongodb.uri", uriNew,
                "spring.data.mongodb.database", dbOld,
                "spring.mongodb.database", dbNew
        );
    }
}