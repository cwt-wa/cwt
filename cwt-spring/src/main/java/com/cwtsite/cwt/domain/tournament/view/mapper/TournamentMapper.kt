package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.view.model.TournamentDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class TournamentMapper {

    fun toDto(tournament: Tournament) = TournamentDto(
        id = tournament.id!!,
        year = LocalDateTime.ofInstant(tournament.created!!, ZoneId.of("UTC")).year,
        goldWinner = tournament.goldWinner?.let { UserMinimalDto(id = it.id!!, username = it.username) },
        silverWinner = tournament.silverWinner?.let { UserMinimalDto(id = it.id!!, username = it.username) },
        bronzeWinner = tournament.bronzeWinner?.let { UserMinimalDto(id = it.id!!, username = it.username) },
        moderators = tournament.moderators.map { UserMinimalDto(id = it.id!!, username = it.username) }
    )
}
