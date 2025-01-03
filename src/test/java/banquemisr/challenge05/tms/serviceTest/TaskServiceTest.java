package banquemisr.challenge05.tms.serviceTest;

import banquemisr.challenge05.tms.repository.TaskRepository;
import banquemisr.challenge05.tms.service.TaskService;
import banquemisr.challenge05.tms.exception.TaskAlreadyExistsException;
import banquemisr.challenge05.tms.exception.TaskDueDateExceededException;
import banquemisr.challenge05.tms.exception.TaskNotFoundException;
import banquemisr.challenge05.tms.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    private final static String TASK_ID = "12345";
    private final static String ANOTHER_TASK_ID = "10101";
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;
    private Task task;
    @Mock
    private MongoTemplate mongoTemplate;

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
    void testFindByTitleAndStatusAndDueDateBetween() {
        String title = "test";
        String status = "pending";
        LocalDate dueDateStart = LocalDate.of(2023, 1, 1);
        LocalDate dueDateEnd = LocalDate.of(2023, 12, 31);
        int page = 0;
        int size = 5;

        Query expectedQuery = new Query()
                .addCriteria(Criteria.where("title").regex(title, "i"))
                .addCriteria(Criteria.where("status").is(status))
                .addCriteria(Criteria.where("dueDate").gte(dueDateStart).lte(dueDateEnd))
                .with(PageRequest.of(page, size));

        Task task1 = new Task(TASK_ID, "Task 1", "Description", "COMPLETED", "LOW", LocalDate.now().plusDays(2), "newTask@example.com");
        Task task2 = new Task(ANOTHER_TASK_ID, "Task 2", "Description", "COMPLETED", "LOW", LocalDate.now().plusDays(2), "newTask@example.com");
        List<Task> tasks = Arrays.asList(task1, task2);

        long count = tasks.size();
        when(mongoTemplate.count(any(Query.class), eq(Task.class))).thenReturn(count);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(tasks);

        // Act
        Page<Task> result = taskService.findByTitleAndStatusAndDueDateBetween(
                title, status, dueDateStart, dueDateEnd, page, size
        );

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Task 1", result.getContent().get(0).getTitle());

        verify(mongoTemplate).count(argThat(query -> query.equals(expectedQuery)), eq(Task.class));
        verify(mongoTemplate).find(argThat(query -> query.equals(expectedQuery)), eq(Task.class));
    }
}
