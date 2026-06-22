package com.navaantrix.vyakhyanLite.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class DataNotFoundException extends RuntimeException{
    private final String errorType;
    private final HttpStatus httpStatus;

    public DataNotFoundException(String message) {
        super(message);
        this.errorType = "DATA_NOT_FOUND";
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public String getErrorType() {
        return errorType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
