package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import java.time.LocalDateTime
import java.time.ZoneId

@DataTransferObject
data class TournamentMinimalDto(
    val id: Long,
    val year: Int,
) {
    companion object {
        fun toDto(tournament: Tournament) = TournamentMinimalDto(
            id = tournament.id!!,
            year = LocalDateTime.ofInstant(tournament.created!!, ZoneId.of("UTC")).year,
        )
    }
}
