package com.navaantrix.vyakhyanLite.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SameDataUpdateException extends RuntimeException {

    private final String errorType;
    private final HttpStatus httpStatus;

    public SameDataUpdateException(String message) {
        super(message);
        this.errorType = "SAME_DATA_UPDATE";
        this.httpStatus = HttpStatus.CONFLICT;
    }

}
