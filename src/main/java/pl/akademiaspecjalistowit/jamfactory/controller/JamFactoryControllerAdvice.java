package pl.akademiaspecjalistowit.jamfactory.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.akademiaspecjalistowit.jamfactory.exception.BusinessException;
import pl.akademiaspecjalistowit.jamfactory.exception.JarException;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;

@ControllerAdvice
public class JamFactoryControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RejectResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new RejectResponse(e.getMessage(), ErrorCode.BUSINESS_ERROR));
    }

    @ExceptionHandler(ProductionException.class)
    public ResponseEntity<RejectResponse> handleProductionException(ProductionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RejectResponse(e.getMessage(), ErrorCode.JAM_PRODUCTION_LIMIT_EXCEEDED));
    }

    @ExceptionHandler(JarException.class)
    public ResponseEntity<RejectResponse> handleJarException(JarException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RejectResponse(e.getMessage(), ErrorCode.INSUFFICIENT_JARS));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RejectResponse {
        private String rejectReason;
        private ErrorCode code;
    }

    enum ErrorCode {
        BUSINESS_ERROR,
        JAM_PRODUCTION_LIMIT_EXCEEDED,
        INSUFFICIENT_JARS
    }
}
