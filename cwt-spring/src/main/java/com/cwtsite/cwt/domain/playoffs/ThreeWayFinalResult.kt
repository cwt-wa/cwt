package com.cwtsite.cwt.domain.playoffs

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.repository.entity.User

data class ThreeWayFinalResult(
        val gold: User,
        val silver: User,
        val bronze: User
) {

    companion object {

        fun fromThreeWayFinalGames(threeWayFinalGames: List<Game>): ThreeWayFinalResult {
            val userToWonGames = threeWayFinalGames
                    .map { listOf(it.homeUser!!, it.awayUser!!) }
                    .flatten()
                    .distinct()
                    .associateBy { threeWayFinalGames.count { game -> game.winner() == it } }
            return ThreeWayFinalResult(
                    gold = userToWonGames[2]!!,
                    silver = userToWonGames[1]!!,
                    bronze = userToWonGames[0]!!)
        }
    }
}
