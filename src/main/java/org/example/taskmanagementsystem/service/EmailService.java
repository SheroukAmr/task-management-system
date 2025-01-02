package org.example.taskmanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendTaskDeadlineReminder(String to, String taskTitle, String dueDate) {
        String subject = "Upcoming Task Deadline Reminder";
        String text = "Reminder: The task '" + taskTitle + "' is due on " + dueDate + ". Please ensure you complete it on time.";
        sendEmail(to, subject, text);
    }

    public void sendTaskUpdateNotification(String to, String taskTitle, String updateMessage) {
        String subject = "Task Update: " + taskTitle;
        String text = "There has been an update to your task: '" + taskTitle + "'.\n" + updateMessage;
        sendEmail(to, subject, text);
    }
}