package com.navaantrix.vyakhyanLite.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BadRequestException extends RuntimeException{

    private final String errorType;
    private final HttpStatus httpStatus;

    public BadRequestException(String message) {
        super(message);
        this.errorType = "BAD_REQUEST";
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BadRequestException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public String getErrorType() {
        return errorType;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
