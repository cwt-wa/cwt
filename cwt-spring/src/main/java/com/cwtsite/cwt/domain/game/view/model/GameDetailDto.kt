package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.view.model.CommentDto
import com.cwtsite.cwt.domain.game.view.model.RatingDto
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.view.model.TournamentDetailDto
import com.cwtsite.cwt.domain.user.repository.entity.User
import java.time.Instant

@DataTransferObject
data class GameDetailDto(
        val id: Long,
        val scoreHome: Int?,
        val scoreAway: Int?,
        val techWin: Boolean,
        val created: Instant,
        val reportedAt: Instant?,
        val modified: Instant,
        val playoff: PlayoffGame?,
        val group: Group?,
        val tournament: TournamentDetailDto,
        val homeUser: User?,
        val awayUser: User?,
        val reporter: User?,
        val ratings: List<RatingDto>,
        val comments: List<CommentDto>,
        val voided: Boolean,
        val isReplayExists: Boolean,
        val replayQuantity: Int?,
        val playoffRoundLocalized: String?
) {
    companion object {
        fun localizePlayoffRound(threeWayFinal: Boolean, playoffsRoundMax: Int, achievedRound: Int): String {
            return if (threeWayFinal) {
                when (achievedRound) {
                    playoffsRoundMax -> "Three-way Final"
                    playoffsRoundMax - 1 -> "Last 6"
                    playoffsRoundMax - 2 -> "Last 12"
                    playoffsRoundMax - 3 -> "Last 24"
                    playoffsRoundMax - 4 -> "Last 48"
                    playoffsRoundMax - 5 -> "Last 96"
                    else -> throw RuntimeException(
                        "No localization for achievedRound $achievedRound with playoffsRoundMax $playoffsRoundMax"
                    )
                }
            } else {
                when (achievedRound) {
                    playoffsRoundMax + 1 -> "Final"
                    playoffsRoundMax -> "Little Final"
                    playoffsRoundMax - 1 -> "Semifinal"
                    playoffsRoundMax - 2 -> "Quarterfinal"
                    playoffsRoundMax - 3 -> "Last 16"
                    playoffsRoundMax - 4 -> "Last 32"
                    playoffsRoundMax - 5 -> "Last 64"
                    playoffsRoundMax - 6 -> "Last 128"
                    else -> throw RuntimeException(
                        "No localization for achievedRound $achievedRound with playoffsRoundMax $playoffsRoundMax"
                    )
                }
            }
        }
    }
}

