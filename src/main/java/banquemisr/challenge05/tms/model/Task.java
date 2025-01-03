package banquemisr.challenge05.tms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Setter
@Getter
@ToString
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

    @NotBlank(message = "Title is mandatory")
    private String assigneeEmail;

    public Task(String id, String title, String description, String status, String priority, LocalDate dueDate, String assigneeEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assigneeEmail = assigneeEmail;
    }

    public Task() {

    }

}
