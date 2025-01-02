package org.example.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.service.EmailService;
import org.example.taskmanagementsystem.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/taskManagement/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private EmailService emailService;
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        emailService.sendTaskCreationNotification(createdTask.getAssigneeEmail(), createdTask.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        emailService.sendTaskUpdateNotification(updatedTask.getAssigneeEmail(), updatedTask.getTitle());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskService.deleteTask(id);
        String assigneeEmail = task.getAssigneeEmail();
        String taskTitle = task.getTitle();
        emailService.sendTaskDeleteNotification(assigneeEmail, taskTitle);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        List<Task> tasks = taskService.searchTasks(title, description);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("Task not found " );
            // Returns 404 if no tasks are found
        }
        return ResponseEntity.ok(tasks);
    }

    // Endpoint to filter tasks with pagination support
    @GetMapping("/filter/page")
    public ResponseEntity<Page<Task>> filterTasksPageable(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate dueDateStart,
            @RequestParam(required = false) LocalDate dueDateEnd,
            @RequestParam int page,
            @RequestParam int size) {
        Page<Task> tasks = taskService.findByTitleAndStatusAndDueDateBetween(title, status, dueDateStart, dueDateEnd, page, size);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("Task not found");
            // Returns 404 if no tasks are found
        }
        return ResponseEntity.ok(tasks);
    }
}
