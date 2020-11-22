package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.view.model.TournamentDetailDto
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import java.util.*

@DataTransferObject
data class GameDetailDto(
        val id: Long,
        val scoreHome: Int?,
        val scoreAway: Int?,
        val techWin: Boolean,
        val created: Date,
        val reportedAt: Date?,
        val modified: Date,
        val playoff: PlayoffGame?,
        val group: Group?,
        val tournament: TournamentDetailDto,
        val homeUser: User?,
        val awayUser: User?,
        val reporter: User?,
        val ratings: List<Rating>,
        val comments: List<Comment>,
        val voided: Boolean,
        val isReplayExists: Boolean,
        val replayQuantity: Int?,
        val playoffRoundLocalized: String?
) {

    companion object {

        fun toDto(game: Game): GameDetailDto {
            return GameDetailDto(
                    id = game.id!!,
                    scoreHome = game.scoreHome,
                    scoreAway = game.scoreAway,
                    techWin = game.techWin,
                    created = game.created!!,
                    reportedAt = game.reportedAt,
                    modified = game.modified!!,
                    playoff = game.playoff,
                    group = game.group,
                    tournament = TournamentDetailDto.toDto(game.tournament),
                    homeUser = game.homeUser,
                    awayUser = game.awayUser,
                    reporter = game.reporter,
                    ratings = game.ratings,
                    comments = game.comments.sortedBy { it.created },
                    voided = game.voided,
                    isReplayExists = game.replay != null,
                    replayQuantity = game.replayQuantity,
                    playoffRoundLocalized = game.playoff?.let { playoff ->
                        localizePlayoffRound(
                                game.tournament.threeWay!!,
                                // tournament max rounds is playoff max rounds minus group phase
                                game.tournament.maxRounds - 1,
                                playoff.round)
                    }
            )
        }

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
                            "No localization for achievedRound $achievedRound with playoffsRoundMax $playoffsRoundMax")
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
                            "No localization for achievedRound $achievedRound with playoffsRoundMax $playoffsRoundMax")
                }
            }

        }

    }
}
