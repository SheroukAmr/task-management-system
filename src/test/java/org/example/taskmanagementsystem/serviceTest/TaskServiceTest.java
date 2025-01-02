package org.example.taskmanagementsystem.serviceTest;

import org.example.taskmanagementsystem.exception.TaskAlreadyExistsException;
import org.example.taskmanagementsystem.exception.TaskDueDateExceededException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.example.taskmanagementsystem.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;
    private Task task;
    private final static String TASK_ID = "12345";
    private final static String ANOTHER_TASK_ID = "10101";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String taskId = "1234";
        task = new Task(TASK_ID, "Sample Task", "This is a sample task", "PENDING", "low", LocalDate.now().plusDays(1), "test@example.com");
    }

    @Test
    void testCreateTask_Success() {
        when(taskRepository.existsByTitle(task.getTitle())).thenReturn(false);
        when(taskRepository.save(task)).thenReturn(task);
        Task createdTask = taskService.createTask(task);
        assertNotNull(createdTask);
        assertEquals(task.getTitle(), createdTask.getTitle());
    }

    @Test
    void testCreateTask_TaskAlreadyExistsException() {
        when(taskRepository.existsByTitle(task.getTitle())).thenReturn(true);
        TaskAlreadyExistsException exception = assertThrows(TaskAlreadyExistsException.class, () -> taskService.createTask(task));
        assertEquals("Task with Title " + task.getTitle() + " already exists.", exception.getMessage());
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Task foundTask = taskService.getTaskById(TASK_ID);
        assertNotNull(foundTask);
        assertEquals(task.getTitle(), foundTask.getTitle());
    }

    @Test
    void testGetTaskById_TaskNotFoundException() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(TASK_ID));
        assertEquals("Task with ID " + TASK_ID + " not found", exception.getMessage());
    }

    //    @Test
//    void testUpdateTask_Success() {
//        // Setup test data
//        Task taskWithValidDueDate = new Task(1L, "Updated Task", "Updated Description", "PENDING", "medium", LocalDate.now().plusDays(2), "test@example.com");
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//        when(taskRepository.save(taskWithValidDueDate)).thenReturn(taskWithValidDueDate);
//        Task updatedTask = taskService.updateTask(1L, taskWithValidDueDate);
//        assertNotNull(updatedTask);  // Ensure that the task is not null
//        assertEquals(taskWithValidDueDate.getDueDate(), updatedTask.getDueDate());  // Verify that the due date was updated}
//    }
    @Test
    void testUpdateTask_TaskDueDateExceededException() {
        Task taskWithPastDueDate = new Task(TASK_ID, "Updated Task", "Updated Description", "PENDING", "HIGH", LocalDate.now().minusDays(1), "test@example.com");
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        TaskDueDateExceededException exception = assertThrows(TaskDueDateExceededException.class, () -> taskService.updateTask(TASK_ID, taskWithPastDueDate));
        assertEquals("Task with ID " + TASK_ID + "can't be update due date exceeded", exception.getMessage());
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        taskService.deleteTask(TASK_ID);
        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository, times(1)).deleteById(TASK_ID);
    }

    @Test
    void testDeleteTask_TaskNotFoundException() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(TASK_ID));
        assertEquals("Task with ID " + TASK_ID + " not found", exception.getMessage());
    }

    @Test
    void testSearchTasks_Success() {
        Task anotherTask = new Task(ANOTHER_TASK_ID, "New Task", "Description", "COMPLETED", "LOW", LocalDate.now().plusDays(2), "newTask@example.com");
        when(taskRepository.findByTitleOrDescription("Task", "Description")).thenReturn(Arrays.asList(task, anotherTask));
        List<Task> tasks = taskService.searchTasks("Task", "Description");
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void testSearchTasks_TaskNotFoundException() {
        when(taskRepository.findByTitleOrDescription("Nonexistent Task", "No description")).thenReturn(Arrays.asList());
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.searchTasks("Nonexistent Task", "No description"));
        assertEquals("No tasks found matching the search criteria.", exception.getMessage());
    }

    @Test
    void testFilterTasks_Success() {
        Task anotherTask = new Task(ANOTHER_TASK_ID, "Another Task", "Description", "COMPLETED", "LOW", LocalDate.now().plusDays(2), "another@example.com");
        Page<Task> page = new PageImpl<>(Arrays.asList(task, anotherTask), PageRequest.of(0, 10), 2); // PageRequest is for pagination, 2 is the total number of elements
        when(taskRepository.findByTitleAndStatusAndDueDateBetween(
                "Task",
                "PENDING",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                PageRequest.of(0, 10)
        )).thenReturn(page);
        Page<Task> tasks = taskService.findByTitleAndStatusAndDueDateBetween(
                "Task",
                "PENDING",
                LocalDate.now(),
                LocalDate.now().plusDays(1), 0, 10
        );
        assertNotNull(tasks, "The result should not be null.");
        assertEquals(2, tasks.getTotalElements(), "Total elements should be 2.");
        assertEquals(2, tasks.getContent().size(), "The content size should be 2.");
    }

    @Test
    void testFilterTasks_TaskNotFoundException() {
        // Create an empty Page using PageImpl to return as a mock
        Page<Task> emptyPage = new PageImpl<>(Collections.emptyList());

        // Mock the repository method to return an empty Page
        when(taskRepository.findByTitleAndStatusAndDueDateBetween(
                "Task",
                "PENDING",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                PageRequest.of(0, 10) // Pageable mock
        )).thenReturn(emptyPage);

        // Perform the action and assert that the exception is thrown
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () ->
                taskService.findByTitleAndStatusAndDueDateBetween(
                        "Task",
                        "PENDING",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        0, 10
                )
        );

        // Verify the exception message
        assertEquals("No tasks found matching the search criteria.", exception.getMessage());
    }

}
