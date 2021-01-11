package com.cwtsite.cwt.security

import com.cwtsite.cwt.core.HttpClient
import org.slf4j.LoggerFactory
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.net.URI


@Service
class SecurityService {

    @Value("\${captcha-secret:#{null}}") private var captchaSecret: String? = null
    @Value("\${firebase-api-key:#{null}}") private var firebaseApiKey: String? = null
    @Value("\${wormnet-channel}") private lateinit var wormnetChannel: String

    @Autowired private lateinit var httpClient: HttpClient

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun verifyToken(captchaToken: String): Boolean {
        if (captchaSecret == null) {
            logger.warn("Not performing CAPTCHA validation as the secret is not configured.")
            return true
        }

        val uri = "https://www.google.com/recaptcha/api/siteverify" +
                  "?secret=${captchaSecret!!}&response=$captchaToken"
        val request = HttpRequest.newBuilder()
             .GET()
             .uri(URI.create(uri))
             .header("Content-Type", "application/json")
             .build();
        val response: HttpResponse<String> = httpClient.request(request, BodyHandlers.ofString())

        val body = response.body()
        if (response.statusCode() != 200) {
            logger.error("Captcha token validation status code ${response.statusCode()} $body");
            return false
        }

        logger.info("Response $body")
        return JSONObject(body).optBoolean("success") == true
    }

    fun verifySecretWord(secretWordFromUser: String) =
            wormnetChannel.split(",").map { it.toLowerCase() }
                    .contains(secretWordFromUser.replace("#", "").toLowerCase())

    /**
     * [Firebase Custom Token REST Authentication](https://zemke.io/firebase-custom-token-rest-auth)
     *
     * @throws RuntimeException REST request has not responded with 200.
     * @throws IllegalStateException Firebase API is not configured.
     * @throws HttpClient.HttpClientException Error from the third-party service.
     */
    @Throws(RuntimeException::class, IllegalStateException::class, HttpClient.HttpClientException::class)
    fun exchangeFirebaseCustomTokenForIdToken(customToken: String): FirebaseIdentityTokenDto {
        firebaseApiKey ?: throw IllegalStateException()
        val uri = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyCustomToken" +
                  "?key=$firebaseApiKey"
        val payload = JSONObject(mapOf("returnSecureToken" to true, "token" to customToken))
        val request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .POST(BodyPublishers.ofString(payload.toString()))
            .header("Content-Type", "application/json")
            .build()
        val response = httpClient.request(request, BodyHandlers.ofString())
        val json = JSONObject(response.body())
        logger.info(json.toString())
        return FirebaseIdentityTokenDto(
                kind = json.optString("kind"),
                idToken = json.getString("idToken"),
                refreshToken = json.getString("refreshToken"),
                expiresIn = json.optString("expiresIn"),
                isNewUser = json.optBoolean("isNewUser"))
    }

    /**
     * @throws IllegalStateException Firebase API is not configured.
     * @throws HttpClient.HttpClientException Error from the third-party service.
     */
    @Throws(IllegalStateException::class, HttpClient.HttpClientException::class)
    fun refreshFirebaseToken(refreshToken: String): FirebaseIdentityTokenDto {
        firebaseApiKey ?: throw IllegalStateException()
        val payload = JSONObject(mapOf(
                "grant_type" to "refresh_token",
                "refresh_token" to refreshToken))
        val uri = "https://securetoken.googleapis.com/v1/token?key=$firebaseApiKey"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .POST(BodyPublishers.ofString(payload.toString()))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response = httpClient.request(request, BodyHandlers.ofString())
        val json = JSONObject(response.body())
        logger.info(json.toString())
        return FirebaseIdentityTokenDto(
                idToken = json.getString("access_token"),
                refreshToken = json.getString("refresh_token"))
    }
}

