package com.cwtsite.cwt.domain.core.exception;

import com.cwtsite.cwt.controller.AbstractRestException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractRestException {

    public BadRequestException() {
        super(null, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
