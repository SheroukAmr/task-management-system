package banquemisr.challenge05.tms.repository;

import banquemisr.challenge05.tms.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    boolean existsByTitle(String name);

    List<Task> findByTitleOrDescription(String title, String description);
    List<Task> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}


