package com.insight.report.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProcessFailureException extends RuntimeException{
    public ProcessFailureException(String resourceName, String fieldName, String message) {
        super(String.format("%s process failed with %s : '%s'", resourceName, fieldName, message));
    }
}
