package in.nimbo.exception;

public class NewsDaoException extends Exception{

    public NewsDaoException(String message, Throwable e) {
        super(message, e);
    }

    public NewsDaoException(Throwable cause) {
        super(cause);
    }

    public NewsDaoException(String message) {
        super(message);
    }
}
