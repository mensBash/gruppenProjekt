package at.ac.tuwien.sepm.assignment.groupphase.exception;

public class PrintFailedException extends Exception {
    public PrintFailedException(String message) {
        super(message);
    }

    public PrintFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
