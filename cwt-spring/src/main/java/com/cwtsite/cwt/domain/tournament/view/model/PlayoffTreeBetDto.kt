package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

@DataTransferObject
data class PlayoffTreeBetDto(
    val id: Long,
    val user: UserMinimalDto,
    val betOnHome: Boolean
) {

    companion object {

        fun toDto(bet: Bet) = PlayoffTreeBetDto(
            id = bet.id!!,
            user = UserMinimalDto.toDto(bet.user),
            betOnHome = bet.betOnHome
        )
    }
}
