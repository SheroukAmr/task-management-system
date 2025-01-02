package org.example.taskmanagementsystem.model;

public class LoginRequest {

    private String userName;
    private String password;

    // Default constructor
    public LoginRequest() {
    }

    // Constructor with parameters
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
