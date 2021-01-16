package com.cwtsite.cwt.domain.game.view.mapper

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.game.view.model.BetDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BetMapper {

    @Autowired private lateinit var gameDetailMapper: GameDetailMapper

    fun toDto(bet: Bet): BetDto = BetDto(
        id = bet.id!!,
        user = UserMinimalDto(id = bet.user!!.id!!, username = bet.user!!.username),
        game = gameDetailMapper.toDto(bet.game),
        betOnHome = bet.betOnHome
    )
}
