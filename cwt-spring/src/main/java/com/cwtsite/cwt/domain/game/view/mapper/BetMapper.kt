package com.cwtsite.cwt.domain.game.view.mapper

import com.cwtsite.cwt.domain.game.view.model.BetDto
import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.domain.game.view.mapper.GameDetailMapper
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

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

