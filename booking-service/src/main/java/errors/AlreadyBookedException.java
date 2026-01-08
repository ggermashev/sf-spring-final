package errors;

public class AlreadyBookedException extends RuntimeException {
    public AlreadyBookedException() {
        super("Already booked");
    }
}
