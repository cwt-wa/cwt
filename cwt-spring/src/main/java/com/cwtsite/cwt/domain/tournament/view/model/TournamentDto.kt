package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.ZoneOffset

@DataTransferObject
data class TournamentDto(
        val id: Long,
        val year: Int,
        val goldWinner: UserMinimalDto?,
        val silverWinner: UserMinimalDto?,
        val bronzeWinner: UserMinimalDto?,
        val moderators: List<UserMinimalDto>
) {
    companion object {
        fun toDto(tournament: Tournament) = TournamentDto(
                id = tournament.id!!,
                year = tournament.created!!.toInstant().atZone(ZoneOffset.UTC).toLocalDate().year,
                goldWinner = tournament.goldWinner?.let { UserMinimalDto.toDto(it) },
                silverWinner = tournament.silverWinner?.let { UserMinimalDto.toDto(it) },
                bronzeWinner = tournament.bronzeWinner?.let { UserMinimalDto.toDto(it) },
                moderators = tournament.moderators.map { UserMinimalDto.toDto(it) }
        )
    }
}
