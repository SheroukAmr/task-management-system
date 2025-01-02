package org.example.taskmanagementsystem.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "app_user") // Use a different name to avoid conflicts

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String userPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

}
