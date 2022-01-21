package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.model.GameMinimalDto

@DataTransferObject
data class MapDto(
    val texture: String?,
    val game: GameMinimalDto,
    val mapPath: String
) {

    companion object {

        fun toDto(game: Game, mapPath: String, mapTexture: String?): MapDto =
            MapDto(
                texture = mapTexture,
                game = GameMinimalDto.toDto(game),
                mapPath = mapPath
            )
    }
}
