package org.example.taskmanagementsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.example.taskmanagementsystem.repository")
public class MongoConfig {
}
