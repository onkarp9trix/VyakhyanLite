package com.navaantrix.vyakhyanLite.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class InternalServerException extends RuntimeException{

    private final String errorType;
    private final HttpStatus httpStatus;

    public InternalServerException(String message) {
        super(message);
        this.errorType = "INTERNAL_SERVER_ERROR";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }


}

