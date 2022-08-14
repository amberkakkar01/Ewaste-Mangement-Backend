package com.groupfive.ewastemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String exception) {
        super(exception);
    }
}