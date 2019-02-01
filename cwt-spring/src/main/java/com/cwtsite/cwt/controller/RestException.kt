package com.cwtsite.cwt.controller

import org.springframework.http.HttpStatus

class RestException(message: String, val status: HttpStatus, cause: Throwable?) : RuntimeException(message, cause)
