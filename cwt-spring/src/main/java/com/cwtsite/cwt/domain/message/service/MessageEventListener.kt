package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.message.entity.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

interface MessageEventListener {

    val listeners: MutableList<(Message) -> Unit>
    val logger: Logger

    fun publish(message: Message)

    fun listen(listener: (Message) -> Unit) {
        synchronized(listener) { listeners.add(listener) }
    }

    fun deafen(listener: (Message) -> Unit) {
        synchronized(listener) { listeners.removeIf { it == listener } }
    }
}

@Component
@Profile("!sync")
class MessageEventListenerAsync : MessageEventListener {

    override val listeners: MutableList<(Message) -> Unit> = mutableListOf()
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun publish(message: Message) {
        logger.info("Publishing $message for ${listeners.size} listeners.")
        thread { listeners.forEach { it(message) } }
    }
}

@Component
@Profile("sync")
class MessageEventListenerSync : MessageEventListener {

    override val listeners: MutableList<(Message) -> Unit> = mutableListOf()
    override val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun publish(message: Message) {
        logger.info("Publishing $message for ${listeners.size} listeners.")
        listeners.forEach { it(message) }
    }
}
