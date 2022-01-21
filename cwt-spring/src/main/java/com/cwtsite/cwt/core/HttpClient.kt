package com.cwtsite.cwt.core

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

@Service
class HttpClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(HttpClientException::class)
    fun <T> request(request: HttpRequest, bodyHandler: BodyHandler<T>): HttpResponse<T> {
        logger.info("Requesting $request")
        val response = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(20))
            .build()
            .send(request, bodyHandler)
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

fun HttpRequest.Builder.postMultipartFormData(boundary: String, data: Map<String, Any>): HttpRequest.Builder {
    val byteArrays = ArrayList<ByteArray>()
    val separator = "--$boundary\r\nContent-Disposition: form-data; name=".toByteArray(StandardCharsets.UTF_8)
    for (entry in data.entries) {
        byteArrays.add(separator)
        when (entry.value) {
            is File -> {
                val file = entry.value as File
                val path = Path.of(file.toURI())
                val mimeType = Files.probeContentType(path)
                byteArrays.add("\"${entry.key}\"; filename=\"${path.fileName}\"\r\nContent-Type: $mimeType\r\n\r\n".toByteArray(StandardCharsets.UTF_8))
                byteArrays.add(Files.readAllBytes(path))
                byteArrays.add("\r\n".toByteArray(StandardCharsets.UTF_8))
            }
            else -> byteArrays.add("\"${entry.key}\"\r\n\r\n${entry.value}\r\n".toByteArray(StandardCharsets.UTF_8))
        }
    }
    byteArrays.add("--$boundary--".toByteArray(StandardCharsets.UTF_8))
    this.header("Content-Type", "multipart/form-data;boundary=$boundary")
        .POST(HttpRequest.BodyPublishers.ofByteArrays(byteArrays))
    return this
}
