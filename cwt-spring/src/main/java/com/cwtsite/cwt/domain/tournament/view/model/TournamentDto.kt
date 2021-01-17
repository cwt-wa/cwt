package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.LocalDateTime
import java.time.ZoneId

@DataTransferObject
data class TournamentDto(
        val id: Long,
        val year: Int,
        val goldWinner: UserMinimalDto?,
        val silverWinner: UserMinimalDto?,
        val bronzeWinner: UserMinimalDto?,
        val moderators: List<UserMinimalDto>
) 

