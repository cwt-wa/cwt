package com.cwtsite.cwt.security

import com.cwtsite.cwt.core.HttpClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import org.json.JSONObject
import java.net.http.HttpRequest
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
     * @throws RuntimeException REST request has not responded with 200.
     * @throws IllegalStateException Firebase API is not configured.
     */
    @Throws(RuntimeException::class, IllegalStateException::class)
    fun exchangeFirebaseCustomTokenForIdToken(customToken: String): FirebaseIdentityTokenDto {
        firebaseApiKey ?: throw IllegalStateException()

        val response = khttp.post(
                url = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyCustomToken",
                json = mapOf("returnSecureToken" to true, "token" to customToken),
                headers = mapOf("Content-Type" to "application/json"),
                params = mapOf("key" to firebaseApiKey!!))

        if (response.statusCode != 200) {
            throw RuntimeException("""Error during exchange of Firebase custom token for an ID token ${response.statusCode}
            |${response.content.toString(Charset.defaultCharset())}
        """.trimMargin())
        }

        val resJson = response.jsonObject
        logger.info(resJson.toString())
        return FirebaseIdentityTokenDto(
                kind = resJson.getString("kind"),
                idToken = resJson.getString("idToken"),
                refreshToken = resJson.getString("refreshToken"),
                expiresIn = resJson.getString("expiresIn"),
                isNewUser = resJson.getBoolean("isNewUser"))
    }

    /**
     * @throws IllegalStateException Firebase API is not configured.
     */
    @Throws(IllegalStateException::class)
    fun refreshFirebaseToken(refreshToken: String): FirebaseIdentityTokenDto {
        firebaseApiKey ?: throw IllegalStateException()

        val response = khttp.post(
                url = "https://securetoken.googleapis.com/v1/token",
                data = mapOf("grant_type" to "refresh_token", "refresh_token" to refreshToken),
                headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
                params = mapOf("key" to firebaseApiKey!!))

        if (response.statusCode != 200) {
            throw RuntimeException("""Error while refreshing Firebase token with HTTP status ${response.statusCode}
            |${response.content.toString(Charset.defaultCharset())}
        """.trimMargin())
        }

        val resJson = response.jsonObject
        logger.info(resJson.toString())
        return FirebaseIdentityTokenDto(
                idToken = resJson.getString("access_token"),
                refreshToken = resJson.getString("refresh_token"))
    }
}
