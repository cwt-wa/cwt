package com.cwtsite.cwt.domain.game.view.mapper

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.model.GameMinimalDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component

@Component
class GameMinimalMapper {

    fun toDto(game: Game): GameMinimalDto =
        GameMinimalDto(
            id = game.id!!,
            scoreHome = game.scoreHome!!,
            scoreAway = game.scoreAway!!,
            techWin = game.techWin,
            created = game.created!!,
            reportedAt = game.reportedAt!!,
            modified = game.modified!!,
            homeUser = UserMinimalDto(id = game.homeUser!!.id!!, username = game.homeUser!!.username),
            awayUser = UserMinimalDto(id = game.awayUser!!.id!!, username = game.awayUser!!.username),
            replayExists = game.replay != null
        )
}
