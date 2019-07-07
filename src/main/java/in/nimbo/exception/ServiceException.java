package in.nimbo.exception;

public class ServiceException extends DoubleConstructorException {

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

    public ServiceException(String message) {
        super(message);
    }
}
