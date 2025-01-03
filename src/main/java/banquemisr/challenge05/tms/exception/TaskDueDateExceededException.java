package banquemisr.challenge05.tms.exception;

public class TaskDueDateExceededException extends RuntimeException {
    public TaskDueDateExceededException(String message) {
        super(message);
    }
}
