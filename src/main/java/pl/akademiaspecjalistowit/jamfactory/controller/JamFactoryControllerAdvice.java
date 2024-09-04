package pl.akademiaspecjalistowit.jamfactory.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.akademiaspecjalistowit.jamfactory.exception.BusinessException;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.exception.JarFactoryHttpClientException;

import java.util.stream.Collectors;

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

    @ExceptionHandler(JarFactoryHttpClientException.class)
    public ResponseEntity<RejectResponse> handleJarFactoryHttpClientException(JarFactoryHttpClientException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RejectResponse(e.getMessage(), ErrorCode.INSUFFICIENT_JARS));
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<RejectResponse> handleValidationExceptions(TransactionSystemException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RejectResponse(ex.getMessage(), ErrorCode.PRECONDITION_FAILED));
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<RejectResponse> handleConstraintViolationException(IllegalArgumentException e) {
//        String errors = e.getConstraintViolations().stream()
//                .map(error -> error.getMessageTemplate())
//                .collect(Collectors.joining("\n"));
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new RejectResponse(errors, ErrorCode.INSUFFICIENT_JARS));
//    }

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
        INSUFFICIENT_JARS,
        PRECONDITION_FAILED
    }
}
