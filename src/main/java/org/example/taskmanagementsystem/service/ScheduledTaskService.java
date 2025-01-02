package org.example.taskmanagementsystem.service;

import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    // This method runs every day at 8 AM to check for upcoming deadlines
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendTaskDeadlineReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusDays(1); // Remind 1 day before the task deadline

        // Find tasks whose deadline is within the next day
        List<Task> tasks = taskRepository.findByDueDateBetween(now, reminderTime);

        // Send reminder email for each task
        for (Task task : tasks) {
            emailService.sendTaskDeadlineReminder(task.getAssigneeEmail(), task.getTitle(), task.getDueDate().toString());
        }
    }
}