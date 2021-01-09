package com.cwtsite.cwt.core

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler

@Service
class HttpClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun <T> request(request: HttpRequest, bodyHandler: BodyHandler<T>): HttpResponse<T> {
        logger.info("Requesting $request")
        return HttpClient.newBuilder().build().send(request, bodyHandler)
    }
}
