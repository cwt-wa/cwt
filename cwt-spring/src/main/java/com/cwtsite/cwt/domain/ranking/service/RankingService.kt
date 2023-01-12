package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.ranking.entity.Ranking
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@Component
class RankingService
@Autowired
constructor(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val rankingRepository: RankingRepository,
    private val tournamentRepository: TournamentRepository,
) {

    fun relrank(games: List<Game>): Map<Long, BigDecimal> {
        val stdin = games
            .filter { it.wasPlayed() }
            .joinToString(
                separator = "\n",
                postfix = "\n"
            ) { "${it.homeUser!!.id},${it.awayUser!!.id},${it.scoreHome},${it.scoreAway}" }
        val cmd = "/Users/lair/relrank"
        val pb = ProcessBuilder(*cmd.split("\\s".toRegex()).toTypedArray())
            .redirectErrorStream(true)
        with(pb.environment()) {
            put(
                "RELRANK_SCALE_MAX",
                (games.distinctBy { it.tournament }.size * MAX_ROUNDS_WON).toString()
            )
        }
        val process = pb.start().apply {
            outputStream.bufferedWriter().apply {
                write(stdin)
                flush()
                close()
            }
            waitFor(1, TimeUnit.MINUTES)
        }
        return process.inputStream.bufferedReader().use { it.readText() }
            .trim()
            .lines()
            .associate {
                val (id, rating) = it.split(",")
                id.toLong() to BigDecimal(rating)
            }
    }

    fun save(relrank: Map<Long, BigDecimal>): List<Ranking> {
        val games = gameRepository.findAll()
        val tournaments = tournamentRepository.findAll()
        val users = userRepository.findAllById(relrank.keys)
        val rankings = users
            .map { Ranking(user = it, points = relrank[it.id]!!) }
            .map { ranking ->
                games
                    .filter { it.wasPlayed() && it.pairingInvolves(ranking.user) }
                    .forEach { g ->
                        if (g.homeUser == ranking.user) {
                            ranking.won += g.scoreHome ?: 0
                            ranking.lost += g.scoreAway ?: 0
                        } else if (g.awayUser == ranking.user) {
                            ranking.won += g.scoreAway ?: 0
                            ranking.lost += g.scoreHome ?: 0
                        }
                        if (ranking.last == null || g.tournament.created!!.isAfter(ranking.last!!.created)) {
                            ranking.last = g.tournament
                        }
                    }
                ranking.played = ranking.won + ranking.lost
                ranking.wonRatio = ranking.won.toDouble() / ranking.played
                ranking.participations = ranking.user?.userStats?.participations ?: 0
                tournaments
                    // TODO conceptually do ranking only for archived tournaments?
                    .filter { it.status == TournamentStatus.FINISHED || it.status == TournamentStatus.ARCHIVED }
                    .forEach {
                        ranking.gold += if (it.goldWinner == ranking.user) 1 else 0
                        ranking.silver += if (it.silverWinner == ranking.user) 1 else 0
                        ranking.bronze += if (it.bronzeWinner == ranking.user) 1 else 0
                    }
                ranking
            }
        val prev = rankingRepository.findAll().sortedByDescending { it.points }
        rankings.sortedByDescending { it.points }.forEachIndexed { index, ranking ->
            val idx = prev.indexOfFirst { it.user == ranking.user }
            if (idx >= 0) {
                ranking.lastDiff = index - idx
            } else {
                // not having participated works as if you'd ended up last
                ranking.lastDiff = -prev.size + index
            }
        }
        return rankingRepository.saveAll(rankings).sortedByDescending { it.points }
    }

    companion object {
        /**
         * Per tournament one can win 3 group stage games, round of last 16,
         * quarterfinal, semifinal and final.
         */
        const val MAX_ROUNDS_WON: Int = 3 * 3 + 3 * 3 + 4
    }
}
