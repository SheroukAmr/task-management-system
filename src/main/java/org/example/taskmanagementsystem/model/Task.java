package org.example.taskmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "Tasks")
public class Task {
    @Id
    private String id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "high|medium|low", message = "Priority must be one of: high, medium, low")
    private String priority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDate dueDate;

    private String assigneeEmail;

    public Task(String id,String title,String description,String status,String priority,LocalDate dueDate,String assigneeEmail) {
        this.id = id;
        this.title=title;
        this.description=description;
        this.status=status;
        this.priority=priority;
        this.dueDate=dueDate;
        this.assigneeEmail=assigneeEmail;
    }

    public Task() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }
}
