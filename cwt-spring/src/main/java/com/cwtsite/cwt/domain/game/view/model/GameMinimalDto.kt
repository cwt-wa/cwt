package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.Instant

@DataTransferObject
data class GameMinimalDto(
    val id: Long,
    val scoreHome: Int,
    val scoreAway: Int,
    val techWin: Boolean,
    val created: Instant,
    val reportedAt: Instant,
    val modified: Instant,
    val homeUser: UserMinimalDto,
    val awayUser: UserMinimalDto,
    val replayExists: Boolean
) {

    companion object {

        fun toDto(game: Game): GameMinimalDto {
            return GameMinimalDto(
                id = game.id!!,
                scoreHome = game.scoreHome!!,
                scoreAway = game.scoreAway!!,
                techWin = game.techWin,
                created = game.created!!,
                reportedAt = game.reportedAt!!,
                modified = game.modified!!,
                homeUser = UserMinimalDto(game.homeUser!!.id!!, game.homeUser!!.username),
                awayUser = UserMinimalDto(game.awayUser!!.id!!, game.awayUser!!.username),
                replayExists = game.replay != null
            )
        }
    }
}
