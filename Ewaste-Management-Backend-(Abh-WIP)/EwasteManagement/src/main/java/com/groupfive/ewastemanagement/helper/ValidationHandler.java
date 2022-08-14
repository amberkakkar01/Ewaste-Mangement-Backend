package com.groupfive.ewastemanagement.helper;

import java.util.HashMap;
import java.util.Map;

import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.dto.error.ValidationErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus("fail");

        Map<String, ValidationErrorMessage> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            ValidationErrorMessage e = new ValidationErrorMessage();

            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            e.setErrorCode("400");
            e.setErrorMessage(message);
            errors.put(fieldName, e);
        });
        responseMessage.setData(errors);

        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }

}
