package com.cwtsite.cwt.core

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService {

    enum class EMAIL_ADDRESS {
        SUPPORT, NOREPLY
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired(required = false) private val mailSender: JavaMailSender? = null

    @Value("\${email-address-domain}") private val emailAddressDomain: String? = null
    @Value("\${email-address-name-support}") private val emailAddressNameSupport: String? = null
    @Value("\${email-address-name-noreply}") private val emailAddressNameNoReply: String? = null

    fun sendMail(message: String, subject: String, to: String, from: EMAIL_ADDRESS) {
        if (mailSender == null) {
            logger.info("No JavaMailSender configured, not sending mails.")
            return
        }

        val simpleMailMessage = with(SimpleMailMessage()) {
            setTo(to)
            setFrom(determineSender(from))
            setSubject(subject)
            text = message
            this
        }

        mailSender.send(simpleMailMessage);
    }

    private fun determineSender(from: EMAIL_ADDRESS): String {
        val domain = emailAddressDomain ?: "example.com"
        val name = when (from) {
            EMAIL_ADDRESS.NOREPLY -> emailAddressNameNoReply
            EMAIL_ADDRESS.SUPPORT -> emailAddressNameSupport
        }
        return "$name@$domain"
    }
}
