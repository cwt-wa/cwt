package com.cwtsite.cwt.core.event.stats

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class GameStatsEventListener : ApplicationListener<GameStatsEvent> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    val subscribers = mutableListOf<SseEmitter>()

    @Override
    override fun onApplicationEvent(event: GameStatsEvent) {
        logger.info("Adding Spring Custom Event to queue " + event.gameStats)
        subscribers.forEach { it.send(event.gameStats.data, MediaType.APPLICATION_STREAM_JSON) }
    }
}
