package com.groupfive.ewastemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message)
    {
        super(message);
    }
}