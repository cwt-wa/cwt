package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.tournament.view.model.MapDto
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.mapper.GameMinimalMapper

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class MapMapper {

    @Autowired private lateinit var gameMinimalMapper: GameMinimalMapper

    fun toDto(game: Game, mapPath: String, mapTexture: String?): MapDto =
            MapDto(
                    texture = mapTexture,
                    game = gameMinimalMapper.toDto(game),
                    mapPath = mapPath)
}

