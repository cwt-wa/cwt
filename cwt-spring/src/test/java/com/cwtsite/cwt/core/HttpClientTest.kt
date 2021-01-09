package com.cwtsite.cwt.core

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

class HttpClientTest {

    private val cut: HttpClient = HttpClient()

    @Test
    fun request() {
        val uri = "http://postman-echo.com/get?hello=world"
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(uri))
            .header("Content-Type", "application/json")
            .build()
        val response: HttpResponse<String> = cut.request(request, BodyHandlers.ofString())
        val json = JSONObject(response.body())
        assertThat(json.getJSONObject("args").getString("hello")).isEqualTo("world")
    }
}
