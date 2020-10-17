package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto

@DataTransferObject
data class MapDto(
        val texture: String?,
        val game: GameDetailDto,
        val mapPath: String) {

    companion object {

        fun toDto(game: Game, mapPath: String, mapTexture: String?): MapDto =
                MapDto(
                        texture = mapTexture,
                        game = GameDetailDto.toDto(

                                game = game,
                                playoffRoundLocalized = when {
                                    game.playoff() -> GameDetailDto.localizePlayoffRound(
                                            game.tournament.threeWay!!,
                                            game.tournament.maxRounds,
                                            game.playoff!!.round)
                                    else -> null
                                }),
                        mapPath = mapPath)
    }
}

