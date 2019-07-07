package in.nimbo.exception;

public class NewsDaoException extends DoubleConstructorException{

    public NewsDaoException(String message, Throwable e) {
        super(message, e);
    }

    public NewsDaoException(String message) {
        super(message);
    }
}
