package com.groupfive.ewastemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class RangeNotSatisfiedException extends RuntimeException{
    public RangeNotSatisfiedException(String exception) {
        super(exception);
    }
}
