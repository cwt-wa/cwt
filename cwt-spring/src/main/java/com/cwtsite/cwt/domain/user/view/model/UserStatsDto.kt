package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.core.toBoolean
import com.cwtsite.cwt.domain.core.DataTransferObject
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import java.io.IOException

@DataTransferObject
data class UserStatsDto(
        val participated: Boolean,
        val year: Int,
        val tournamentId: Long,
        val tournamentMaxRound: Int,
        val threeWayFinal: Boolean,
        val round: Int,
        val locRound: String
) {

    companion object {

        fun toDtos(timeline: String): List<UserStatsDto> {
            val timelineIterator: Iterator<JsonNode>
            try {
                timelineIterator = ObjectMapper().readTree("[$timeline]").elements()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            val dtos = mutableListOf<UserStatsDto>()

            while (timelineIterator.hasNext()) {
                val elem = timelineIterator.next()
                val threeWayFinal = elem.get(2).asInt().toBoolean()
                val playoffsRoundsMax = elem.get(3).asInt()
                val playoffsRoundAchieved = elem.get(4).asInt()

                dtos.add(UserStatsDto(
                        participated = playoffsRoundsMax != 0,
                        year = elem.get(1).asInt(),
                        tournamentId = elem.get(0).asLong(),
                        tournamentMaxRound = playoffsRoundsMax,
                        round = playoffsRoundAchieved,
                        threeWayFinal = threeWayFinal,
                        locRound = locRound(threeWayFinal, playoffsRoundsMax, playoffsRoundAchieved)
                ))
            }

            return dtos
        }

        private fun locRound(threeWayFinal: Boolean, playoffsRoundMax: Int, achievedRound: Int): String {
            return when (achievedRound) {
                0 -> "Not attended"
                1 -> "Group"
                else -> {
                    if (threeWayFinal) {
                        when (achievedRound) {
                            playoffsRoundMax + 3 -> "Champion"
                            playoffsRoundMax + 2 -> "Runner-up"
                            playoffsRoundMax + 1 -> "Third"
                            playoffsRoundMax -> "Last 6"
                            playoffsRoundMax - 1 -> "Last 12"
                            playoffsRoundMax - 2 -> "Last 24"
                            playoffsRoundMax - 3 -> "Last 48"
                            playoffsRoundMax - 4 -> "Last 96"
                            else -> throw RuntimeException()
                        }
                    } else {
                        when (achievedRound) {
                            playoffsRoundMax + 2 -> "Champion"
                            playoffsRoundMax + 1 -> "Runner-up"
                            playoffsRoundMax -> "Third"
                            playoffsRoundMax - 1 -> "Semifinal"
                            playoffsRoundMax - 2 -> "Quarterfinal"
                            playoffsRoundMax - 3 -> "Last 16"
                            playoffsRoundMax - 4 -> "Last 32"
                            playoffsRoundMax - 5 -> "Last 64"
                            playoffsRoundMax - 6 -> "Last 128"
                            else -> throw RuntimeException()
                        }
                    }
                }
            }
        }
    }
}
