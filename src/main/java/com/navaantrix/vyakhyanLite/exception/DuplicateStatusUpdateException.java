package com.navaantrix.vyakhyanLite.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class DuplicateStatusUpdateException  extends RuntimeException{

    private final String errorType="already has status ID";
    private final String message;
    private final HttpStatus httpStatus=HttpStatus.CONFLICT;

    public DuplicateStatusUpdateException(final String message) {
        super(message);
        this.message = message;
    }

}
