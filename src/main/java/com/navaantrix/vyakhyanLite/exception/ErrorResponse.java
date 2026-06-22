package com.navaantrix.vyakhyanLite.exception;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String errorType;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;

}
