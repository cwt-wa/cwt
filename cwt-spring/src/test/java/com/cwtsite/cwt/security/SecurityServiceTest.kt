package com.cwtsite.cwt.security

import com.cwtsite.cwt.core.HttpClient
import com.cwtsite.cwt.test.MockitoUtils.safeEq
import com.cwtsite.cwt.test.MockitoUtils.safeCapture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import org.assertj.core.api.Assertions.assertThat
import java.net.http.HttpResponse
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import org.mockito.ArgumentCaptor

@ExtendWith(MockitoExtension::class)
class SecurityServiceTest {

    @InjectMocks private lateinit var securityService: SecurityService

    @Mock private lateinit var httpClient: HttpClient

    @Captor private lateinit var requestCaptor: ArgumentCaptor<HttpRequest>

    private val captchaSecret: String = ""
    private val wormnetChannel: String = "chocolate-chimpanze,chocolatechimpanze,chocolate chimpanze"

    @Test
    fun verifySecretWord() {
        ReflectionTestUtils.setField(securityService, "captchaSecret", captchaSecret)
        ReflectionTestUtils.setField(securityService, "wormnetChannel", wormnetChannel)

        assertThat(securityService.verifySecretWord("#chocolatechimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("chocolatechimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("chocolate chimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("#chocolate-chimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("chocolate-chimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("#chocolate chimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("chocolateChimpanze")).isTrue()
        assertThat(securityService.verifySecretWord("candyApe")).isFalse()
    }

    @Test
    fun verifyToken() {
        ReflectionTestUtils.setField(securityService, "captchaSecret", captchaSecret)
        val captchaToken = "captchaToken"
        val httpResponse = mock(HttpResponse::class.java) as HttpResponse<String>
        val body = """{"success": true}"""
        `when`(httpResponse.statusCode()).thenReturn(200)
        `when`(httpResponse.body()).thenReturn(body)
        `when`(httpClient.request(safeCapture(requestCaptor), safeEq(BodyHandlers.ofString())))
                .thenReturn(httpResponse)
        val actual = securityService.verifyToken(captchaToken)
        assertThat(requestCaptor.value.method()).isEqualTo("GET")
        assertThat(requestCaptor.value.headers().firstValue("content-type")).isPresent()
        assertThat(requestCaptor.value.headers().firstValue("content-type").get())
                .isEqualTo("application/json")
        assertThat(requestCaptor.value.uri()).satisfies {
            val expectedUri = "https://www.google.com/recaptcha/api/siteverify" +
                      "?secret=$captchaSecret&response=$captchaToken"
            assertThat(it.toString()).isEqualTo(expectedUri)
        }
        assertThat(actual).isTrue()
    }
}

