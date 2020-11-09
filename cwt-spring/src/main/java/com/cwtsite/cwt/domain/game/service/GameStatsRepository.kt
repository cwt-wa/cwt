package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameStatsRepository : JpaRepository<GameStats, Long> {

    fun findAllByGame(game: Game): List<GameStats>

    fun findAllByTexture(texture: String, page: Pageable): Page<GameStats>

    fun countByTexture(texture: String?): Long

    @Query("select distinct s.texture from GameStats s")
    fun findTextureDistinct(): List<String?>
}

