package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.game.entity.GameStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameStatsRepository : JpaRepository<GameStats, Long>
