package com.cwtsite.cwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<?> handleAllExceptions(RestException ex, WebRequest request) {
        final RestError restError = new RestError(
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                ex.getStatus().value(),
                new Date());
        logger.error(ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(restError);
    }
}
