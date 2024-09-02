package pl.akademiaspecjalistowit.jamfactory.exception;

public class JarException extends RuntimeException{

    public JarException(String message) {
        super(message);
    }

    public JarException(String message, Throwable cause) {
        super(message, cause);
    }
}
