package com.kahago.kahagoservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 4 Des 2019
 */
@ResponseStatus(HttpStatus.ACCEPTED)
public class InProgressException extends RuntimeException {
    public InProgressException(String s) {
        super(s);
    }
}
