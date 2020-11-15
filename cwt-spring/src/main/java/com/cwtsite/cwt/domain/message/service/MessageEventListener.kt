package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.message.entity.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageEventListener {

    private var listener: ((Message) -> Unit)? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun listen(fn: (message: Message) -> Unit) {
        logger.info("Register listener.")
        listener = fn
    }

    fun publish(message: Message) {
        logger.info("Publishing $message for listener $listener.")
        listener?.let { it(message) }
    }
}
