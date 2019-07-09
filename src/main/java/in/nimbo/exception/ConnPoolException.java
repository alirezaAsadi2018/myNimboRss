package in.nimbo.exception;

public class ConnPoolException extends DoubleConstructorException {
    public ConnPoolException(String message, Throwable e) {
        super(message, e);
    }

    public ConnPoolException(String message) {
        super(message);
    }
}
