package com.cwtsite.cwt.core.event.stats

import com.cwtsite.cwt.domain.game.entity.GameStats
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class GameStatsEventPublisher {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    fun publish(gameStats: GameStats, isLast: Boolean) {
        logger.info("Publishing game stats for game ${gameStats.game!!.id!!}.")
        applicationEventPublisher.publishEvent(GameStatsEvent(this, gameStats, isLast))
    }
}
