package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.view.model.TournamentDetailDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component

@Component
class TournamentDetailMapper {

    fun toDto(tournament: Tournament) = TournamentDetailDto(
        id = tournament.id,
        status = tournament.status,
        review = tournament.review,
        maxRounds = tournament.maxRounds,
        numOfGroupAdvancing = tournament.numOfGroupAdvancing,
        threeWay = tournament.threeWay,
        created = tournament.created,
        bronzeWinner = tournament.bronzeWinner?.let { UserMinimalDto(id = it.id!!, username = it.username) },
        silverWinner = tournament.silverWinner?.let { UserMinimalDto(id = it.id!!, username = it.username) },
        goldWinner = tournament.goldWinner?.let { UserMinimalDto(id = it.id!!, username = it.username) },
        moderators = tournament.moderators.map { UserMinimalDto(id = it.id!!, username = it.username) }.toSet()
    )
}
