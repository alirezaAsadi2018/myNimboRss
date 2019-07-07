package in.nimbo.exception;

public class UrlDaoException extends DoubleConstructorException{
    public UrlDaoException(String message) {
        super(message);
    }

    public UrlDaoException(String message, Throwable e) {
        super(message, e);
    }
}
