package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import java.time.Instant

@DataTransferObject
data class PlayoffGameDto(
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
        val ratings: List<Rating>,
        val comments: List<Comment>,
        val isReplayExists: Boolean,
        val bets: List<PlayoffTreeBetDto>,
        val playoffRoundLocalized: String?
) 

