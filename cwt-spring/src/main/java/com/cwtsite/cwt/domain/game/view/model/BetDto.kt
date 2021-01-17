package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

@DataTransferObject
data class BetDto(
        val id: Long,
        val user: UserMinimalDto,
        val game: GameDetailDto,
        val betOnHome: Boolean
) 

