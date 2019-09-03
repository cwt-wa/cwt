package com.cwtsite.cwt.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.Charset

@Service
class CaptchaService {

    @Value("\${captcha-secret}") private lateinit var captchaSecret: String

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun verifyToken(captchaToken: String): Boolean {
        val response = khttp.get(
                url = "https://www.google.com/recaptcha/api/siteverify",
                params = mapOf("secret" to captchaSecret, "response" to captchaToken))

        if (response.statusCode != 200) {
            logger.error("""Captcha token validation status code ${response.statusCode}
                |${response.content.toString(Charset.defaultCharset())}
            """.trimMargin())
            return false
        }

        val jsonObject = response.jsonObject
        logger.info(jsonObject.toString())
        return jsonObject.getBoolean("success")
    }
}
