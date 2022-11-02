package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.test.EntityDefaults
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.BufferedReader
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class StreamServiceTest {

    @InjectMocks private lateinit var cut: StreamService
    @Mock private lateinit var streamRepository: StreamRepository
    @Mock private lateinit var channelRepository: ChannelRepository
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var gameRepository: GameRepository

    data class TestRecord(
        val user1: String?,
        val user2: String?,
        val streamTitle: String,
        val matchingStreamTitles: List<String>
    )

    companion object {

        private lateinit var usernames: List<String>
        private lateinit var testSet: Set<TestRecord>
        private var setup: Boolean = false
    }

    @Before
    fun setUp() {
        if (setup) return
        val tsv = StreamServiceTest::class.java.getResourceAsStream("testset.tsv")
            .bufferedReader().use(BufferedReader::readText)
        testSet = tsv.lines().filter { it.isNotEmpty() }.map { ln ->
            val split = ln.split("\t")
            val usernames = split[0].split(',')
            TestRecord(
                user1 = usernames[0],
                user2 = usernames[1],
                streamTitle = split[1],
                matchingStreamTitles = split.subList(2, split.size)
            )
        }.toSet()
        usernames = StreamServiceTest::class.java.getResourceAsStream("usernames.txt")
            .bufferedReader().use(BufferedReader::readText)
            .lines()
        setup = true
    }

    @Test
    fun findMatchingGame() {
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val streamCreatedAt = tournament.created!!.plus(5, ChronoUnit.DAYS)
        `when`(streamRepository.findHomeUsernamesForUnlinkedGames(tournament))
            .thenReturn(usernames.subList(0, 30))
        `when`(streamRepository.findAwayUsernamesForUnlinkedGames(tournament))
            .thenReturn(usernames.subList(30, usernames.size))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)
        testSet.forEach { record ->
            val stream = EntityDefaults.stream(
                title = record.streamTitle,
                createdAt = formatter.format(streamCreatedAt)
            )
            `when`(tournamentService.getCurrentTournamentAt(stream.createdAtAsInstant()))
                .thenReturn(tournament)
            if (record.user1 == null || record.user2 == null) {
                assertThat(cut.findMatchingGame(stream)).isNull()
                return@forEach
            }
            val user1 = EntityDefaults.user(username = record.user1)
            val user2 = EntityDefaults.user(username = record.user2)
            `when`(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(user1).thenReturn(user2)
            val game = EntityDefaults.game(
                homeUser = user1,
                awayUser = user2,
                reportedAt = Instant.now(),
                scoreHome = 1,
                scoreAway = 3,
                playoff = PlayoffGame(1, 1, 1)
            )
            `when`(gameRepository.findGame(user1, user2, tournament)).thenReturn(listOf(game))
            assertThat(cut.findMatchingGame(stream))
                .satisfies { matchingGame ->
                    assertThat(matchingGame).isNotNull
                    assertThat(matchingGame).satisfiesAnyOf(
                        {
                            assertThat(it!!.homeUser).isEqualTo(user1)
                            assertThat(it.awayUser).isEqualTo(user2)
                        },
                        {
                            assertThat(it!!.homeUser).isEqualTo(user2)
                            assertThat(it.awayUser).isEqualTo(user1)
                        }
                    )
                }
        }
    }

    @Test
    fun findMatchingStream() {
        val streams = testSet.map { EntityDefaults.stream(title = it.streamTitle, id = "${UUID.randomUUID()}") }
        `when`(streamRepository.findByGameIsNull()).thenReturn(streams)
        testSet.forEach { record ->
            if (record.user1 == null || record.user2 == null) {
                assertThat(cut.findMatchingStreams(EntityDefaults.game(reportedAt = Instant.ofEpochMilli(1413672557000))))
                    .isEmpty()
                return@forEach
            }
            val game = with(EntityDefaults.game()) {
                homeUser?.let { it.username = record.user1 }
                awayUser?.let { it.username = record.user2 }
                reportedAt = Instant.ofEpochMilli(1413672557000) // couple of minutes after the stream createdAt
                this
            }
            assertThat(cut.findMatchingStreams(game)).extracting<String> { it.title }
                .containsExactlyInAnyOrder(*record.matchingStreamTitles.toTypedArray())
        }
    }
}
