package com.cwtsite.cwt.core

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStream
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler

@Service
class HttpClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun <T> request(request: HttpRequest, bodyHandler: BodyHandler<T>): HttpResponse<T> {
        logger.info("Requesting $request")
        val response = HttpClient.newBuilder().build().send(request, bodyHandler)
        if (!response.statusCode().toString().startsWith("2")) {
            val body = response.body()
            val message = when (body) {
                is String -> body
                is InputStream -> body.bufferedReader().use(BufferedReader::readText)
                is ByteArray -> body.decodeToString()
                else -> {
                    logger.warn("Cannot parse body of type ${body!!::class.simpleName}")
                    ""
                }
            }
            throw HttpClientException(message, response.statusCode())
        }
        return response
    }

    inner class HttpClientException(message: String, public val statusCode: Int) : RuntimeException(message)
}
