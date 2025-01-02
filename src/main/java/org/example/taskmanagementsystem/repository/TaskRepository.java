package org.example.taskmanagementsystem.repository;

import org.example.taskmanagementsystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    boolean existsByTitle(String name);
    List<Task> findByTitleOrDescription(String title, String description);
    // Pagination with filtering
    Page<Task> findByTitleAndStatusAndDueDateBetween(String title, String status, LocalDate dueDateStart, LocalDate dueDateEnd, Pageable pageable);

    List<Task> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}


