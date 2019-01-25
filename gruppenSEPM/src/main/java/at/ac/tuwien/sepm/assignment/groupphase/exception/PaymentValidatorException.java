package at.ac.tuwien.sepm.assignment.groupphase.exception;

public class PaymentValidatorException extends Exception {

    public PaymentValidatorException(String message) {
        super(message);
    }

    public PaymentValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentValidatorException(Throwable cause) {
        super(cause);
    }
}
