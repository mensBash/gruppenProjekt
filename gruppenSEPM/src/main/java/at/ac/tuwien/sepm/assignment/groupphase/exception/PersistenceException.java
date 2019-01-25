package at.ac.tuwien.sepm.assignment.groupphase.exception;

public class PersistenceException extends Exception {

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);


    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
