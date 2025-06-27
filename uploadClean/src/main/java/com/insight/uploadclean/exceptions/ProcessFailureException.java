package com.insight.uploadclean.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProcessFailureException extends RuntimeException{
    public ProcessFailureException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s process failed with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
