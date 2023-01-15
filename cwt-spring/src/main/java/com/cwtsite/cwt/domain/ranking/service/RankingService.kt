package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.ranking.entity.Ranking
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus.ARCHIVED
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus.FINISHED
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
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
    @Value("\${relrank.executable:#{null}}")
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
            // TODO tweak some more variables to get chuv rated better
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
                        if (ranking.lastTournament == null ||
                            g.tournament.created!!.isAfter(ranking.lastTournament!!.created)
                        ) {
                            ranking.lastTournament = g.tournament
                        }
                    }
                ranking.played = ranking.won + ranking.lost
                ranking.wonRatio = ranking.won.toDouble() / ranking.played
                ranking.participations = ranking.user?.userStats?.participations ?: 0
                tournaments
                    .forEach {
                        ranking.gold += if (it.goldWinner == ranking.user) 1 else 0
                        ranking.silver += if (it.silverWinner == ranking.user) 1 else 0
                        ranking.bronze += if (it.bronzeWinner == ranking.user) 1 else 0
                    }
                ranking
            }
        // comparing new position to position of after last finished/archived tournament
        val prev = rankingRepository.findAll()
        val userPlaceAssoc: MutableMap<User, Int> = mutableMapOf()
        rankings.sortedByDescending { it.points }.forEachIndexed { newPlace, ranking ->
            userPlaceAssoc[ranking.user!!] =
                newPlace - (prev.find { it.user == ranking.user }?.lastPlace ?: prev.size)
            ranking.lastDiff = userPlaceAssoc[ranking.user!!]!!
        }
        val prevRef = prev
            .mapNotNull { it.lastTournament }
            .filter { it.status == ARCHIVED || it.status == FINISHED }
            .maxByOrNull { it.created!! }!!
        val newRef = rankings
            .mapNotNull { it.lastTournament }
            .filter { it.status == ARCHIVED || it.status == FINISHED }
            .maxByOrNull { it.created!! }!!
        if (newRef != prevRef) {
            rankings.forEach { it.lastPlace = userPlaceAssoc[it.user]!! }
        }
        rankingRepository.deleteAll()
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
