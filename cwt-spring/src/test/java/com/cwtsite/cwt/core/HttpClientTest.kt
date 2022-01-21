package com.cwtsite.cwt.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigInteger
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.net.http.HttpResponse.BodyHandlers
import java.util.Random

class HttpClientTest {

    private val cut: HttpClient = HttpClient()

    enum class TestBodyHandlers(public val bodyHandler: BodyHandler<*>) {
        STRING(BodyHandlers.ofString()),
        INPUT_STREAM(BodyHandlers.ofInputStream()),
        BYTE_ARRAY(BodyHandlers.ofByteArray());
    }

    @Test
    fun request_ok() {
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

    @ParameterizedTest
    @EnumSource(HttpClientTest.TestBodyHandlers::class)
    fun request_404(testBodyHandler: TestBodyHandlers) {
        val uri = "https://postman-echo.com/status/404"
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(uri))
            .header("Content-Type", "application/json")
            .build()
        val throwable = catchThrowable { cut.request(request, testBodyHandler.bodyHandler) }
        assertThat(throwable).isExactlyInstanceOf(HttpClient.HttpClientException::class.java)
        throwable is HttpClient.HttpClientException
        // assertThat(throwable.statusCode).isEqualTo(404) // TODO
        assertThat(throwable.message).isEqualTo("""{"status":404}""")
    }

    @Test
    fun request_form() {
        val request = HttpRequest.newBuilder()
            .postMultipartFormData(
                BigInteger(35, Random()).toString(),
                mapOf("hello" to "world")
            )
            .uri(URI.create("http://postman-echo.com/post"))
            .header("Content-Type", "multipart/form-data")
            .build()
        val response: HttpResponse<String> = cut.request(request, BodyHandlers.ofString())
        val json = JSONObject(response.body())
        assertThat(json.optJSONObject("form")?.optString("hello")).isEqualTo("world")
        assertThat(response.statusCode()).isEqualTo(200)
    }
}
