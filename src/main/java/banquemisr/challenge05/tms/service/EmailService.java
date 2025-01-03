package banquemisr.challenge05.tms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendTaskCreationNotification(String toEmail, String taskTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Task Created: " + taskTitle);
        message.setText("A new task titled '" + taskTitle + "' has been assigned to you.");

        emailSender.send(message);
    }

    public void sendTaskUpdateNotification(String toEmail, String taskTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Task Updated: " + taskTitle);
        message.setText("Your task has been updated.");

        emailSender.send(message);
    }

    public void sendTaskDeleteNotification(String toEmail, String taskTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Task delete: " + taskTitle);
        message.setText("Your task has been deleted: '" + taskTitle);
        emailSender.send(message);
    }

    public void sendTaskDeadlineReminder(String toEmail, String taskTitle, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Upcoming Task due date Reminder");
        message.setText("Reminder: The task '" + taskTitle + "' is due on " + dueDate + ". Please ensure you complete it on time.");
        emailSender.send(message);
    }
}