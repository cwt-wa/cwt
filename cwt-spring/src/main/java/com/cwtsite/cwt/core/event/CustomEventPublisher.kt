package com.cwtsite.cwt.core.event

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class CustomEventPublisher {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    fun publishEvent(message: String) {
        logger.info("Publishing custom event.")
        val customSpringEvent = CustomEvent(this, message)
        applicationEventPublisher.publishEvent(customSpringEvent)
    }
}
