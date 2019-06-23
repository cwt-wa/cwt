package com.cwtsite.cwt.domain.bet.service

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface BetRepository : JpaRepository<Bet, Long> {

    fun findByUserAndGame(user: User, game: Game): Bet?
}
