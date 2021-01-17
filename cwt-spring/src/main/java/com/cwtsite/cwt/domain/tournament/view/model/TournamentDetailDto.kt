package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.Instant

@DataTransferObject
data class TournamentDetailDto(
        val id: Long?,
        val status: TournamentStatus,
        val review: String?,
        val maxRounds: Int,
        val numOfGroupAdvancing: Int?,
        val threeWay: Boolean?,
        val created: Instant?,
        val bronzeWinner: UserMinimalDto?,
        val silverWinner: UserMinimalDto?,
        val goldWinner: UserMinimalDto?,
        val moderators: Set<UserMinimalDto>?
) 

