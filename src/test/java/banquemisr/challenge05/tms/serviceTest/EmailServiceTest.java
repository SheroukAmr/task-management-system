package banquemisr.challenge05.tms.serviceTest;

import banquemisr.challenge05.tms.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;
    @InjectMocks
    private EmailService emailService;

    final static String FROM_EMAIL = "fromEmail";
    final static String TO_EMAIL = "test@example.com";
    final static String SUBJECT = "Test Subject";
    final static String TEXT = "Test message";
    final static String DUE_DATE = "2025-01-10";
    final static String TASK_TITLE = "Task 1";

    @Test
    public void testSendEmail() {
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));
        SimpleMailMessage message = new SimpleMailMessage();
        emailSender.send(message);
        message.setFrom(FROM_EMAIL);
        message.setTo(TO_EMAIL);
        message.setSubject(SUBJECT);
        message.setText(TEXT);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage capturedMessage = captor.getValue();
        assertEquals(TO_EMAIL, capturedMessage.getTo()[0]);
        assertEquals(SUBJECT, capturedMessage.getSubject());
        assertEquals(TEXT, capturedMessage.getText());
    }

    @Test
    public void testSendTaskDeadlineReminder() {
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));
        emailService.sendTaskDeadlineReminder(TO_EMAIL, TASK_TITLE, DUE_DATE);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage capturedMessage = captor.getValue();
        assertEquals(TO_EMAIL, capturedMessage.getTo()[0]);
        assertEquals("Upcoming Task due date Reminder", capturedMessage.getSubject());
        assertEquals("Reminder: The task 'Task 1' is due on 2025-01-10. Please ensure you complete it on time.", capturedMessage.getText());
    }

    @Test
    void testSendTaskUpdateNotification() {
        emailService.sendTaskUpdateNotification(TO_EMAIL, TASK_TITLE);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage capturedMessage = captor.getValue();
        assertEquals(TO_EMAIL, capturedMessage.getTo()[0]);
        assertEquals("Task Updated: " + TASK_TITLE, capturedMessage.getSubject());
        assertEquals("Your task has been updated.", capturedMessage.getText());
    }

    @Test
    void testSendTaskDeleteNotification() {

        emailService.sendTaskDeleteNotification(TO_EMAIL, TASK_TITLE);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage capturedMessage = captor.getValue();
        assertEquals(TO_EMAIL, capturedMessage.getTo()[0]);
        assertEquals("Task delete: " + TASK_TITLE, capturedMessage.getSubject());
        assertEquals("Your task has been deleted: '" + TASK_TITLE, capturedMessage.getText());
    }

}
