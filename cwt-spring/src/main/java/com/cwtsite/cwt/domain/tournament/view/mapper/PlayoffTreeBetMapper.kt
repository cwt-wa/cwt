package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.tournament.view.model.PlayoffTreeBetDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class PlayoffTreeBetMapper {

    fun toDto(bet: Bet) = PlayoffTreeBetDto(
        id = bet.id!!,
        user = UserMinimalDto(id = bet.user.id!!, username = bet.user.username),
        betOnHome = bet.betOnHome
    )
}

