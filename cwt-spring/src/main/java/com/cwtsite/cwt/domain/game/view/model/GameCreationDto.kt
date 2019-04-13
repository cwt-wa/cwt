package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User

data class GameCreationDto(
        var id: Long? = null,
        var homeUser: Long,
        var awayUser: Long,
        var playoff: PlayoffDto? = null
) {

    companion object {

        fun fromDto(dto: GameCreationDto, home: User, away: User,
                    tournament: Tournament) = Game(
                id = dto.id,
                homeUser = home,
                awayUser = away,
                tournament = tournament,
                playoff = PlayoffGame(
                        round = dto.playoff!!.round,
                        spot = dto.playoff!!.spot
                )
        )

        fun toDto(game: Game) = GameCreationDto(
                id = game.id,
                homeUser = game.homeUser!!.id!!,
                awayUser = game.awayUser!!.id!!,
                playoff = if (game.playoff != null) PlayoffDto.toDto(game.playoff!!) else null
        )
    }
}
