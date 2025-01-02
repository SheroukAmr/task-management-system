package org.example.taskmanagementsystem.exception;

public class TaskDueDateExceededException extends RuntimeException {
    public TaskDueDateExceededException(String message) {
        super(message);
    }
}
