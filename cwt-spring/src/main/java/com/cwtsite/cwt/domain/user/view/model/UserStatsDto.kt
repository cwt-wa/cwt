package com.cwtsite.cwt.domain.user.view.model

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
        val round: Int,
        val locRound: String
) {

    companion object {

        fun toDtos(timeline: String?): List<UserStatsDto> {
            val timelineIterator: Iterator<JsonNode>
            try {
                timelineIterator = ObjectMapper().readTree("[$timeline]").elements()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            val dtos = mutableListOf<UserStatsDto>()
            while (timelineIterator.hasNext()) {
                val elem = timelineIterator.next()
                val tournamentMaxRound = elem.get(2).asInt()
                val round = elem.get(3).asInt()

                dtos.add(UserStatsDto(
                        participated = elem.get(3).asInt() != 0,
                        year = elem.get(1).asInt(),
                        tournamentId = elem.get(0).asLong(),
                        tournamentMaxRound = tournamentMaxRound,
                        round = round,
                        locRound = locRound(tournamentMaxRound, round)
                ))
            }

            return dtos
        }

        fun locRound(tournamentMaxRound: Int, round: Int): String {
            val integerStringHashMap = mutableMapOf<Int, String>()
            val roundOfLastSixteen = tournamentMaxRound - 3

            integerStringHashMap[tournamentMaxRound + 2] = "Champion"
            integerStringHashMap[tournamentMaxRound + 1] = "Runner-up"
            integerStringHashMap[tournamentMaxRound] = "Third"
            integerStringHashMap[tournamentMaxRound - 1] = "Semifinal"
            integerStringHashMap[tournamentMaxRound - 2] = "Quarterfinal"
            integerStringHashMap[roundOfLastSixteen] = "Last Sixteen"
            integerStringHashMap[1] = "Group"
            integerStringHashMap[0] = "Not attended"

            var multiplier = 2
            for (i in roundOfLastSixteen - 1 downTo 2) {
                integerStringHashMap[i] = "Last " + java.lang.Double.valueOf(16 * Math.pow(2.0, (multiplier - 1).toDouble())).toInt()
                multiplier++
            }

            return integerStringHashMap[round]!!
        }
    }
}
