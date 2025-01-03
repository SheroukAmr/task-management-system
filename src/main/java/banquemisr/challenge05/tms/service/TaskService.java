package banquemisr.challenge05.tms.service;

import banquemisr.challenge05.tms.exception.TaskAlreadyExistsException;
import banquemisr.challenge05.tms.exception.TaskDueDateExceededException;
import banquemisr.challenge05.tms.exception.TaskNotFoundException;
import banquemisr.challenge05.tms.model.Task;
import banquemisr.challenge05.tms.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    public TaskService(TaskRepository taskRepository, MongoTemplate mongoTemplate) {
        this.taskRepository = taskRepository;
        this.mongoTemplate = mongoTemplate;
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
        return taskRepository.save(task);
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
    public Page<Task> findByTitleAndStatusAndDueDateBetween(String title, String status, LocalDate dueDateStart, LocalDate dueDateEnd, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = new Query();
        if (title != null && !title.isEmpty()) {
            query.addCriteria(Criteria.where("title").regex(title, "i")); // "i" makes it case-insensitive
        }
        if (status != null && !status.isEmpty()) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (dueDateStart != null && dueDateEnd != null) {
            query.addCriteria(Criteria.where("dueDate").gte(dueDateStart).lte(dueDateEnd));
        }
        query.with(pageable);
        long count = mongoTemplate.count(query, Task.class);
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        return new PageImpl<>(tasks, pageable, count);
    }
}
