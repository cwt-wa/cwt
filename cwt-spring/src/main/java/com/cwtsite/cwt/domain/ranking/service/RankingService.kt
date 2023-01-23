package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.ranking.entity.Ranking
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus.ARCHIVED
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus.FINISHED
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.math.BigDecimal
import java.time.Instant
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
    @Value("\${cwt.relrank.executable:#{null}}")
    private val relrankExec: String? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun relrank(games: List<Game>): Map<Long, BigDecimal>? {
        if (relrankExec == null) {
            logger.warn("No relrank executable, cannot generate ranking")
            return null
        }
        val stdin = games
            .filter { it.wasPlayed() }
            .joinToString(
                separator = "\n",
                postfix = "\n"
            ) { "${it.homeUser!!.id},${it.awayUser!!.id},${it.scoreHome},${it.scoreAway}" }
        val pb = ProcessBuilder(relrankExec)
            .directory(File(System.getProperty("java.io.tmpdir")))
            .redirectErrorStream(true)
        with(pb.environment()) {
            put("RELRANK_RELREL", "21")
            put("RELRANK_SCALE_MAX", (games.distinctBy { it.tournament }.size * MAX_ROUNDS_WON).toString())
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

    fun save(games: List<Game>, relrank: Map<Long, BigDecimal>): List<Ranking> {
        val prev = rankingRepository.findAll()
        if ((prev.mapNotNull { it.modified }.maxOrNull() ?: Instant.MIN)
                .isAfter((games.mapNotNull { it.reportedAt }.maxOrNull() ?: Instant.MIN))
        ) {
            logger.warn("There are no new games.")
            return prev
        }
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
                        if (ranking.lastTournament == null ||
                            g.tournament.created!!.isAfter(ranking.lastTournament!!.created)
                        ) {
                            ranking.lastTournament = g.tournament
                        }
                    }
                ranking.played = ranking.won + ranking.lost
                ranking.wonRatio = ranking.won.toDouble() / ranking.played
                ranking.participations = ranking.user.userStats?.participations ?: 0
                tournaments
                    .forEach {
                        ranking.gold += if (it.goldWinner == ranking.user) 1 else 0
                        ranking.silver += if (it.silverWinner == ranking.user) 1 else 0
                        ranking.bronze += if (it.bronzeWinner == ranking.user) 1 else 0
                    }
                ranking
            }
        // comparing new position to position of after last finished/archived tournament
        val prevRef = prev
            .mapNotNull { it.lastTournament }
            .filter { it.status == ARCHIVED || it.status == FINISHED }
            .maxByOrNull { it.created!! }
        val newRef = rankings
            .mapNotNull { it.lastTournament }
            .filter { it.status == ARCHIVED || it.status == FINISHED }
            .maxByOrNull { it.created!! }!!
        rankings.sortedByDescending { it.points }.forEachIndexed { newPlace, ranking ->
            val prevUser = prev.find { it.user == ranking.user }
            ranking.diff(
                newPlace,
                newRef,
                prevRef,
                prevUser?.lastPlace,
                prev.size,
            )
        }
        rankingRepository.deleteAll()
        return rankingRepository.saveAll(rankings).sortedByDescending { it.points }
    }

    fun findRelevantGames() = gameRepository.findAll().filter { it.wasPlayed() }

    fun findRankings() = rankingRepository.findAll().sortedByDescending { it.points }

    companion object {
        /**
         * Per tournament one can win 3 group stage games, round of last 16,
         * quarterfinal, semifinal and final.
         */
        const val MAX_ROUNDS_WON: Int = 3 * 3 + 3 * 3 + 4
    }
}
