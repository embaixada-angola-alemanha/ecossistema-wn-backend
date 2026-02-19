package ao.gov.embaixada.wn.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }

    public InvalidStateTransitionException(String from, String to) {
        super("Invalid state transition from " + from + " to " + to);
    }
}
