package com.navaantrix.vyakhyanLite.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFound(DataNotFoundException ex, HttpServletRequest request) {
        return buildResponse(ex.getErrorType(), ex.getMessage(), ex.getHttpStatus(),request);
    }

    @ExceptionHandler(DuplicateStatusUpdateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateStatusUpdateException ex, HttpServletRequest request) {
        return buildResponse(ex.getErrorType(), ex.getMessage(), ex.getHttpStatus(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(ex.getErrorType(), ex.getMessage(), ex.getHttpStatus(), request);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternal(InternalServerException ex, HttpServletRequest request) {
        return buildResponse(ex.getErrorType(), ex.getMessage(), ex.getHttpStatus(), request);
    }

    @ExceptionHandler(SameDataUpdateException.class)
    public ResponseEntity<ErrorResponse> handleSameData(SameDataUpdateException ex,HttpServletRequest request) {
        return buildResponse(ex.getErrorType(), ex.getMessage(), ex.getHttpStatus(),request);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponse> hadleDuplicatData(DuplicateDataException ex, HttpServletRequest request){
        return buildResponse(ex.getErrorType(), ex.getMessage(), ex.getHttpStatus(),request);
    }


    private ResponseEntity<ErrorResponse> buildResponse(
            String errorType,
            String message,
            HttpStatus status,
            HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .errorType(errorType)
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, status);
    }

}

