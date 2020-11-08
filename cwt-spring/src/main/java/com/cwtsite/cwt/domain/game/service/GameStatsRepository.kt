package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@Repository
interface GameStatsRepository : JpaRepository<GameStats, Long> {

    fun findAllByGame(game: Game): List<GameStats>

    @Query("select distinct s.texture from GameStats s")
    fun findTextureDistinct(): List<String>
}

