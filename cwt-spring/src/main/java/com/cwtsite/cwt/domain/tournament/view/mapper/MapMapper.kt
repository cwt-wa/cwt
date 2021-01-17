package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.mapper.GameMinimalMapper
import com.cwtsite.cwt.domain.tournament.view.model.MapDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class MapMapper {

    @Autowired private lateinit var gameMinimalMapper: GameMinimalMapper

    fun toDto(game: Game, mapPath: String, mapTexture: String?): MapDto =
        MapDto(
            texture = mapTexture,
            game = gameMinimalMapper.toDto(game),
            mapPath = mapPath
        )
}
