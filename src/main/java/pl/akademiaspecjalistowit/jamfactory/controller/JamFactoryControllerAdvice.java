package pl.akademiaspecjalistowit.jamfactory.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.akademiaspecjalistowit.jamfactory.exception.JamFactoryException;

@ControllerAdvice
public class JamFactoryControllerAdvice {

    @ExceptionHandler(JamFactoryException.class)
    public ResponseEntity<RejectResponse> handleTravelException(JamFactoryException e) {
        RejectResponse rejectResponse = new RejectResponse("przekraczajaca zdolnośc produkcyjna");
        return new ResponseEntity<>(rejectResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RejectResponse {
        private String rejectReason;
    }
}
