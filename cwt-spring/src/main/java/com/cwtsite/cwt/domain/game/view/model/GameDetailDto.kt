package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import java.util.*

class GameDetailDto(
        val id: Long,
        val scoreHome: Int,
        val scoreAway: Int,
        val techWin: Boolean,
        val created: Date,
        val modified: Date,
        val playoff: PlayoffGame?,
        val group: Group?,
        val tournament: Tournament,
        val homeUser: User,
        val awayUser: User,
        val reporter: User,
        val ratings: List<Rating>,
        val comments: List<Comment>,
        val isReplayExists: Boolean,
        val playoffRoundLocalized: String?
) {

    companion object {

        fun toDto(game: Game, playoffRoundLocalized: String?): GameDetailDto {
            return GameDetailDto(
                    id = game.id!!,
                    scoreHome = game.scoreHome!!,
                    scoreAway = game.scoreAway!!,
                    techWin = game.techWin,
                    created = game.created!!,
                    modified = game.modified!!,
                    playoff = game.playoff,
                    group = game.group,
                    tournament = game.tournament,
                    homeUser = game.homeUser!!,
                    awayUser = game.awayUser!!,
                    reporter = game.reporter!!,
                    ratings = game.ratings,
                    comments = game.comments,
                    isReplayExists = game.replay != null,
                    playoffRoundLocalized = playoffRoundLocalized
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
                    else -> throw RuntimeException()
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
                    else -> throw RuntimeException()
                }
            }

        }

    }
}
