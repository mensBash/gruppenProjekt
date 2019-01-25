package at.ac.tuwien.sepm.assignment.groupphase.exception;

public class CustomerException extends Exception {

    public CustomerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerException(String message) {
        super(message);
    }
}
