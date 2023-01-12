package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.ranking.service.RankingService.Companion.MAX_ROUNDS_WON
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.UserStats
import com.cwtsite.cwt.test.EntityDefaults.game
import com.cwtsite.cwt.test.EntityDefaults.ranking
import com.cwtsite.cwt.test.EntityDefaults.tournament
import com.cwtsite.cwt.test.EntityDefaults.user
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assume
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.anyList
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.slf4j.LoggerFactory
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(MockitoJUnitRunner::class)
class RankingServiceTest {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @InjectMocks
    private lateinit var rankingService: RankingService

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var rankingRepository: RankingRepository

    @Mock
    private lateinit var tournamentRepository: TournamentRepository

    @Test
    fun testRelrank() {
        val games = listOf(
            game(
                homeUser = user(id = 1),
                awayUser = user(id = 2),
                scoreHome = 1,
                scoreAway = 2,
            ),
            game(
                homeUser = user(id = 3),
                awayUser = user(id = 4),
                scoreHome = 4,
                scoreAway = 1,
            ),
        )
        val relrankExec = System.getenv("RELRANK_EXEC")
        val warn = "no relrank executable, did not test this unit."
        logger.warn(warn)
        Assume.assumeThat(warn, relrankExec, `is`(notNullValue()))
        logger.info("Using relrank executable: $relrankExec")
        ReflectionTestUtils.setField(rankingService, "relrankExec", relrankExec)
        assertThat(rankingService.relrank(games)).satisfies { r ->
            assertThat(r.maxOf { it.value }).isEqualTo(BigDecimal(1 * MAX_ROUNDS_WON))
            assertThat(r).hasSize(4)
            assertThat(r.keys).containsExactlyInAnyOrder(1, 2, 3, 4)
        }
    }

    @Test
    fun testSave() {
        val users = (1..4).map {
            user(id = it.toLong()).also { u ->
                u.userStats = with(UserStats()) {
                    user = u
                    userId = u.id
                    participations = u.id!!.toInt()
                    this
                }
            }
        }
        val relrank = users
            .zip(arrayOf("4234.1234", "3341.2341", "1412.3412", "2321.4321"))
            .associate { it.first.id!! to BigDecimal(it.second) }
        val tournaments = arrayOf(1L to 2002, 2L to 2003).map {
            tournament(
                id = it.first,
                created = LocalDateTime.of(it.second, 10, 1, 13, 32).toInstant(ZoneOffset.UTC),
                status = TournamentStatus.ARCHIVED,
                goldWinner = users[0],
                silverWinner = users[1],
                bronzeWinner = users[2],
            )
        }
        `when`(gameRepository.findAll())
            .thenReturn(
                listOf(
                    game(
                        homeUser = users[0],
                        awayUser = users[1],
                        scoreHome = 1,
                        scoreAway = 2,
                        tournament = tournaments[0],
                    ),
                    game(
                        homeUser = users[2],
                        awayUser = users[3],
                        scoreHome = 4,
                        scoreAway = 1,
                        tournament = tournaments[1],
                    )
                )
            )
        `when`(tournamentRepository.findAll())
            .thenReturn(listOf(tournaments[0], tournaments[1]))
        `when`(rankingRepository.saveAll(anyList())).thenAnswer { it.arguments[0] }
        `when`(userRepository.findAllById(users.map { it.id }.toSet())).thenReturn(users)
        val prev = listOf(users[0] to 4, users[1] to 3, users[2] to 2).associate {
            it.first to ranking(
                id = it.first.id!!,
                user = it.first,
                points = BigDecimal(it.second)
            )
        }
        `when`(rankingRepository.findAll()).thenReturn(prev.values.toList())
        val act = rankingService.save(relrank)
        assertThat(act.map { it.user }).containsExactlyInAnyOrder(*users.toTypedArray())
        act.forEach {
            when (it.user) {
                users[0] -> {
                    assertThat(it.gold).isEqualTo(2)
                    assertThat(it.silver).isEqualTo(0)
                    assertThat(it.bronze).isEqualTo(0)
                    assertThat(it.won).isEqualTo(1)
                    assertThat(it.lost).isEqualTo(2)
                    assertThat(it.played).isEqualTo(3)
                    assertThat(it.wonRatio).isCloseTo(.333, within(.001))
                    assertThat(it.points).isGreaterThan(BigDecimal.ZERO)
                    assertThat(it.participations).isEqualTo(users[0].userStats!!.participations)
                    assertThat(it.last).isEqualTo(tournaments[0])
                    assertThat(it.lastDiff).isEqualTo(0)
                }

                users[1] -> {
                    assertThat(it.gold).isEqualTo(0)
                    assertThat(it.silver).isEqualTo(2)
                    assertThat(it.bronze).isEqualTo(0)
                    assertThat(it.won).isEqualTo(2)
                    assertThat(it.lost).isEqualTo(1)
                    assertThat(it.played).isEqualTo(3)
                    assertThat(it.wonRatio).isCloseTo(.666, within(.001))
                    assertThat(it.points).isGreaterThan(BigDecimal.ZERO)
                    assertThat(it.participations).isEqualTo(users[1].userStats!!.participations)
                    assertThat(it.last).isEqualTo(tournaments[0])
                    assertThat(it.lastDiff).isEqualTo(0)
                }

                users[2] -> {
                    assertThat(it.gold).isEqualTo(0)
                    assertThat(it.silver).isEqualTo(0)
                    assertThat(it.bronze).isEqualTo(2)
                    assertThat(it.won).isEqualTo(4)
                    assertThat(it.lost).isEqualTo(1)
                    assertThat(it.played).isEqualTo(5)
                    assertThat(it.wonRatio).isCloseTo(.8, within(.1))
                    assertThat(it.points).isGreaterThan(BigDecimal.ZERO)
                    assertThat(it.participations).isEqualTo(users[2].userStats!!.participations)
                    assertThat(it.last).isEqualTo(tournaments[1])
                    assertThat(it.lastDiff).isEqualTo(1)
                }

                users[3] -> {
                    assertThat(it.gold).isEqualTo(0)
                    assertThat(it.silver).isEqualTo(0)
                    assertThat(it.bronze).isEqualTo(0)
                    assertThat(it.won).isEqualTo(1)
                    assertThat(it.lost).isEqualTo(4)
                    assertThat(it.played).isEqualTo(5)
                    assertThat(it.wonRatio).isCloseTo(.2, within(.1))
                    assertThat(it.points).isGreaterThan(BigDecimal.ZERO)
                    assertThat(it.participations).isEqualTo(users[3].userStats!!.participations)
                    assertThat(it.last).isEqualTo(tournaments[1])
                    assertThat(it.lastDiff).isEqualTo(-1) // didn't participate
                }
            }
        }
    }
}
