package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.game.service.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@Component
class RankingService
@Autowired
constructor(
    private val gameRepository: GameRepository,
) {

    fun relrank(): List<Pair<Long, BigDecimal>> {
        val games = gameRepository.findAll()
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
            put("RELRANK_SCALE_MAX", "5000")
        }
        val process = pb.start().apply {
            outputStream.bufferedWriter().apply {
                write(stdin)
                flush()
                close()
            }
            waitFor(1, TimeUnit.MINUTES)
        }
        val txt = process.inputStream.bufferedReader().use { it.readText() }
        return txt
            .trim()
            .lines()
            .map {
                val (id, rating) = it.split(",")
                id.toLong() to BigDecimal(rating)
            }
    }
}
