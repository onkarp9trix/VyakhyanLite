package com.navaantrix.vyakhyanLite.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateDataException extends RuntimeException{

    private final String errorType;
    private final HttpStatus httpStatus;

    public DuplicateDataException(String message) {
        super(message);
        this.errorType = "DUPLICATE_DATA";
        this.httpStatus = HttpStatus.CONFLICT; // 409 Conflict
    }

    public DuplicateDataException(String message, String errorType, HttpStatus httpStatus) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = httpStatus;
    }

    public String getErrorType() {
        return errorType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
