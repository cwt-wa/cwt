package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.util.*

@DataTransferObject
data class TournamentDetailDto(
        val id: Long?,
        val status: TournamentStatus,
        val review: String?,
        val maxRounds: Int,
        val numOfGroupAdvancing: Int?,
        val threeWay: Boolean?,
        val created: Date?,
        val bronzeWinner: UserMinimalDto?,
        val silverWinner: UserMinimalDto?,
        val goldWinner: UserMinimalDto?,
        val moderators: Set<UserMinimalDto>?
) {
    companion object {
        fun toDto(tournament: Tournament) = TournamentDetailDto(
                id = tournament.id,
                status = tournament.status,
                review = tournament.review,
                maxRounds = tournament.maxRounds,
                numOfGroupAdvancing = tournament.numOfGroupAdvancing,
                threeWay = tournament.threeWay,
                created = tournament.created,
                bronzeWinner = tournament.bronzeWinner?.let { UserMinimalDto.toDto(it) },
                silverWinner = tournament.silverWinner?.let { UserMinimalDto.toDto(it) },
                goldWinner = tournament.goldWinner?.let { UserMinimalDto.toDto(it) },
                moderators = tournament.moderators.map { UserMinimalDto.toDto(it) }.toSet()
        )
    }
}
