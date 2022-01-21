package com.cwtsite.cwt.core.event.stats

import com.cwtsite.cwt.domain.game.entity.GameStats
import org.springframework.context.ApplicationEvent

class GameStatsEvent(source: Any, val gameStats: GameStats, val isLast: Boolean) : ApplicationEvent(source)
