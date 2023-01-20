package com.cwtsite.cwt.domain.ranking.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.ranking.entity.Ranking
import com.cwtsite.cwt.domain.tournament.view.model.TournamentMinimalDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.math.MathContext
import java.math.RoundingMode
import java.time.Instant
import kotlin.math.roundToInt

@DataTransferObject
data class RankingDto(
    val user: UserMinimalDto,
    val lastTournament: TournamentMinimalDto,
    val lastPlace: Int,
    val lastDiff: Int,
    val points: Int,
    val participations: Int,
    val gold: Int,
    val silver: Int,
    val bronze: Int,
    val played: Int,
    val won: Int,
    val lost: Int,
    val wonRatio: Int,
    val modified: Instant,
) {

    companion object {
        fun toDto(ranking: Ranking) = RankingDto(
            UserMinimalDto.toDto(ranking.user),
            lastTournament = TournamentMinimalDto.toDto(ranking.lastTournament!!),
            lastPlace = ranking.lastPlace,
            lastDiff = ranking.lastDiff,
            points = ranking.points.round(MathContext(0, RoundingMode.HALF_UP)).toInt(),
            participations = ranking.participations,
            gold = ranking.gold,
            silver = ranking.silver,
            bronze = ranking.bronze,
            played = ranking.played,
            won = ranking.won,
            lost = ranking.lost,
            wonRatio = (ranking.wonRatio * 100).roundToInt(),
            modified = ranking.modified!!,
        )
    }
}
