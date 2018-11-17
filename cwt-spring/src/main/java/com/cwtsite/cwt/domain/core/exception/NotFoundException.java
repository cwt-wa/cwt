package com.cwtsite.cwt.domain.core.exception;

import com.cwtsite.cwt.controller.AbstractRestException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractRestException {

    public NotFoundException() {
        super(null, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
