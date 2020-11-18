package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.message.entity.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageEventListener {

    private val listeners: MutableList<(Message) -> Unit> = mutableListOf()

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publish(message: Message) {
        logger.info("Publishing $message for ${listeners.size} listeners.")
        listeners.forEach { it(message) }
    }

    fun listen(listener: (Message) -> Unit) {
        synchronized(listener) { listeners.add(listener) }
    }

    fun deafen(listener: (Message) -> Unit) {
        synchronized(listener) { listeners.removeIf { it == listener } }
    }
}
