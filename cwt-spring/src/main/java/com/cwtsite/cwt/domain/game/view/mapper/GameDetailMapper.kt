package com.cwtsite.cwt.domain.game.view.mapper

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.tournament.view.mapper.TournamentDetailMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class GameDetailMapper {

    @Autowired private lateinit var tournamentDetailMapper: TournamentDetailMapper
    @Autowired private lateinit var ratingMapper: RatingMapper
    @Autowired private lateinit var commentMapper: CommentMapper

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
            tournament = tournamentDetailMapper.toDto(game.tournament),
            homeUser = game.homeUser,
            awayUser = game.awayUser,
            reporter = game.reporter,
            ratings = game.ratings.map { ratingMapper.toDto(it) },
            comments = game.comments.sortedBy { it.created }.map { commentMapper.toDto(it) },
            voided = game.voided,
            isReplayExists = game.replay != null,
            replayQuantity = game.replayQuantity,
            playoffRoundLocalized = game.playoff?.let { playoff ->
                localizePlayoffRound(
                    game.tournament.threeWay!!,
                    // tournament max rounds is playoff max rounds minus group phase
                    game.tournament.maxRounds - 1,
                    playoff.round
                )
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
