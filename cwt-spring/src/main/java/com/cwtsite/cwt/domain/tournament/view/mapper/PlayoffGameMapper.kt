package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.mapper.GameDetailMapper
import com.cwtsite.cwt.domain.tournament.view.model.PlayoffGameDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class PlayoffGameMapper {

    @Autowired private lateinit var gameDetailMapper: GameDetailMapper
    @Autowired private lateinit var tournamentDetailMapper: TournamentDetailMapper
    @Autowired private lateinit var playoffTreeBetMapper: PlayoffTreeBetMapper

    fun toDto(game: Game) = PlayoffGameDto(
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
        ratings = game.ratings,
        comments = game.comments,
        isReplayExists = game.replay != null,
        bets = game.bets.map { playoffTreeBetMapper.toDto(it) },
        playoffRoundLocalized = gameDetailMapper.localizePlayoffRound(
            game.tournament.threeWay!!, game.tournament.maxRounds - 1, game.playoff!!.round
        )
    )
}
