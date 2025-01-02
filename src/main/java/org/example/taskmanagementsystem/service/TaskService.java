package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.exception.TaskAlreadyExistsException;
import org.example.taskmanagementsystem.exception.TaskDueDateExceededException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static org.testng.TestRunner.PriorityWeight.priority;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    public Task createTask(Task task) {
        if (taskRepository.existsByTitle(task.getTitle())) {
            throw new TaskAlreadyExistsException("Task with Title " + task.getTitle() + " already exists.");
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException("Task with ID " + id + " not found"));
    }

    public Task updateTask(String id, Task taskDetails) {
        LocalDate today = LocalDate.now();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        if (taskDetails.getDueDate().isBefore(today)) {
            throw new TaskDueDateExceededException("Task with ID " + id + "can't be update due date exceeded");
        } else task.setDueDate(taskDetails.getDueDate());
        task.setAssigneeEmail(taskDetails.getAssigneeEmail());
        // Debug: Print the task before saving
        System.out.println("Task to be saved: " + task);

        // Save the task
        Task savedTask = taskRepository.save(task);

        // Debug: Print the saved task
        System.out.println("Saved task: " + savedTask);

        return savedTask;
    }

    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        taskRepository.deleteById(id);
    }

    public List<Task> searchTasks(String title, String description) {
        List<Task> tasks = taskRepository.findByTitleOrDescription(title, description);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("No tasks found matching the search criteria.");
        }
        return tasks;

    }
    // Filter tasks with pagination support
    public Page<Task> findByTitleAndStatusAndDueDateBetween(String title, String status, LocalDate dueDateStart, LocalDate dueDateEnd,int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Adjust page size as needed
        Page<Task> pages = taskRepository.findByTitleAndStatusAndDueDateBetween(
                title, status, dueDateStart, dueDateEnd, pageable
        );

        if (pages.isEmpty()) {
            throw new TaskNotFoundException("No tasks found matching the search criteria.");
        }
        return pages;
    }
}
