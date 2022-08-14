package com.groupfive.ewastemanagement.exception;

import com.groupfive.ewastemanagement.dto.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorMessage> tokenException(InvalidTokenException exception){
        return new ResponseEntity<>(new ErrorMessage("Invalid Token", exception.getMessage(), new Date()),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> incorrectInputException(BadRequestException exception){
        return new ResponseEntity<>(new ErrorMessage("Bad Request", exception.getMessage(),new Date()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateUpdationException.class)
    public ResponseEntity<ErrorMessage> duplicateUpdateException(DuplicateUpdationException exception){
        return new ResponseEntity<>(new ErrorMessage("Duplicate Updation", exception.getMessage(),new Date()),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidDomainException.class)
    public ResponseEntity<ErrorMessage> invalidDomainException(InvalidDomainException exception){
        return new ResponseEntity<>(new ErrorMessage("Invalid Domain", exception.getMessage(),new Date()),HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorMessage> invalidUserException(InvalidUserException exception){
        return new ResponseEntity<>(new ErrorMessage("Invalid User", exception.getMessage(),new Date()),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<ErrorMessage> noDataException(NoDataException exception){
        return new ResponseEntity<>(new ErrorMessage("No Data", exception.getMessage(),new Date()),HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotFoundException exception){
        return new ResponseEntity<>(new ErrorMessage("Not Found", exception.getMessage(), new Date()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RangeNotSatisfiedException.class)
    public ResponseEntity<ErrorMessage> outOfRangeException(RangeNotSatisfiedException exception){
        return new ResponseEntity<>(new ErrorMessage("Range Not Satisfied", exception.getMessage(),new Date()),HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException exception){
        return new ResponseEntity<>(new ErrorMessage("Bad Request", Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage(),new Date()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorMessage> accountLockedException(AccountLockedException exception){
        return new ResponseEntity<>(new ErrorMessage("Account Locked", exception.getMessage(),new Date()),HttpStatus.REQUEST_TIMEOUT);
    }
}