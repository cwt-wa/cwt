package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.GroupStanding

@DataTransferObject
data class StandingDto(
        val id: Long,
        val points: Int,
        val games: Int,
        val gameRatio: Int,
        val roundRatio: Int,
        val user: UserMinimalDto
) 

