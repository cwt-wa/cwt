package com.cwtsite.cwt.controller;

import org.springframework.http.HttpStatus;

public abstract class AbstractRestException extends RuntimeException {

    private HttpStatus status;

    public AbstractRestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
