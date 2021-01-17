package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

@DataTransferObject
data class PlayoffTreeBetDto(
        val id: Long,
        val user: UserMinimalDto,
        val betOnHome: Boolean
) 

