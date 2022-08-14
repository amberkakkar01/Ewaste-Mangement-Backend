package com.groupfive.ewastemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoDataException extends RuntimeException {
    public NoDataException(String message) {
        super(message);
    }
}