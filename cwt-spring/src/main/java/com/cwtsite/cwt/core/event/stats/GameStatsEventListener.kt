package com.cwtsite.cwt.core.event.stats

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class GameStatsEventListener : ApplicationListener<GameStatsEvent> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val subscribers = mutableListOf<GameStatSubscriber>()

    @Override
    override fun onApplicationEvent(event: GameStatsEvent) {
        logger.info("Adding Spring Custom Event to queue " + event.gameStats)
        subscribers
                .filter { it.gameId == event.gameStats.game!!.id }
                .forEach { it.callback(event.gameStats.data) }
    }

    fun subscribe(subscriber: GameStatSubscriber) {
        subscribers.add(subscriber)
    }
}
