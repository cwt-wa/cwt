package com.cwtsite.cwt.core.event

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class CustomEventListener : ApplicationListener<CustomEvent> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    val queue = mutableListOf<String>() // todo thread-safety?

    @Override
    override fun onApplicationEvent(event: CustomEvent) {
        logger.info("Adding Spring Custom Event to queue " + event.message)
        queue.add(event.message)
    }
}
