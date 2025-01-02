package org.example.taskmanagementsystem;

import org.example.taskmanagementsystem.model.AuthorizationRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagementSystemApplication {

    public static void main(String[] args) {
        AuthorizationRequest authRequest = new AuthorizationRequest();
        SpringApplication.run(TaskManagementSystemApplication.class, args);
        System.out.println(authRequest.getUserName());
    }

}
