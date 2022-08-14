package com.groupfive.ewastemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateUpdationException extends RuntimeException {
    public DuplicateUpdationException(String message) {
        super(message);
    }
}