package pl.akademiaspecjalistowit.jamfactory.exception;

public class JamPlanProductionServiceException extends RuntimeException{

    public JamPlanProductionServiceException(String message) {
        super(message);
    }

    public JamPlanProductionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
