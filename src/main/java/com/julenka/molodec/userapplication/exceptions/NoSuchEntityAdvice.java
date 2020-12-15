package com.julenka.molodec.userapplication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoSuchEntityAdvice {
    @ResponseBody
    @ExceptionHandler(NoSuchEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)

    public String NoSuchEntityExceptionHandler (NoSuchEntityException ex){
        return ex.getMessage();
    }
}
