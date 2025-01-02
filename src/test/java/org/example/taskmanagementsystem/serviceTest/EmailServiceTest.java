package org.example.taskmanagementsystem.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import jakarta.mail.internet.MimeMessage;
import org.example.taskmanagementsystem.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;
    @InjectMocks
    private EmailService emailService;
    @Test
    public void testSendEmail() {
        // Arrange: Mock the send method to do nothing for SimpleMailMessage
        doNothing().when(emailSender).send(any(SimpleMailMessage.class)); // Mock send with SimpleMailMessage
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fromEmail");
        message.setTo("test@example.com");
        message.setSubject("Test Subject");
        message.setText("Test message");
        emailSender.send(message);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage capturedMessage = captor.getValue();
        assertEquals(message.getTo()[0], capturedMessage.getTo()[0]);
        assertEquals(message.getSubject(), capturedMessage.getSubject());
        assertEquals(message.getText(), capturedMessage.getText());
    }
    @Test
    public void testSendTaskDeadlineReminder() {
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));
        String to = "test@example.com";
        String taskTitle = "Task 1";
        String dueDate = "2025-01-10";
        emailService.sendTaskDeadlineReminder(to, taskTitle, dueDate);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage capturedMessage = captor.getValue();
        assertEquals(to, capturedMessage.getTo()[0]);
        assertEquals("Upcoming Task due date Reminder", capturedMessage.getSubject());
        assertEquals("Reminder: The task 'Task 1' is due on 2025-01-10. Please ensure you complete it on time.", capturedMessage.getText());
    }

}
