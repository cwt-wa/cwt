package com.cwtsite.cwt.domain.playoffs

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.repository.entity.User

data class ThreeWayFinalResult(
    val gold: User,
    val silver: User,
    val bronze: User
) {

    companion object {

        @Throws(TiedThreeWayFinalResult::class)
        fun fromThreeWayFinalGames(threeWayFinalGames: List<Game>): ThreeWayFinalResult {
            val userToWonGames = threeWayFinalGames
                .map { listOf(it.homeUser!!, it.awayUser!!) }
                .flatten()
                .distinct()
                .associateBy { threeWayFinalGames.count { game -> game.winner() == it } }

            if (userToWonGames.size == 1 && userToWonGames.entries.first().key == 1) {
                throw TiedThreeWayFinalResult()
            }

            return ThreeWayFinalResult(
                gold = userToWonGames.getValue(2),
                silver = userToWonGames.getValue(1),
                bronze = userToWonGames.getValue(0)
            )
        }
    }
}

class TiedThreeWayFinalResult : RuntimeException("All three-way finalists have won one game each.")
