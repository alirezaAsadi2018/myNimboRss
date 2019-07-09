package in.nimbo.exception;

public class DoubleConstructorException extends Exception{
    public DoubleConstructorException(Throwable cause) {
        super(cause);
    }

    public DoubleConstructorException(String message, Throwable e){
        super(message, e);
    }
    public DoubleConstructorException(String message){
        super(message);
    }
}
