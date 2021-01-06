package com.cwtsite.cwt.integration

import com.cwtsite.cwt.core.TwitchScheduler
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.stream.service.ChannelRepository
import com.cwtsite.cwt.domain.stream.service.StreamRepository
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchVideoDto

import com.fasterxml.jackson.databind.ObjectMapper

import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@Sql("stream.sql")
class TwitchJobTest {

    @MockBean private lateinit var twitchService: TwitchService
    @Autowired private lateinit var twitchScheduler: TwitchScheduler
    @Autowired private lateinit var streamRepository: StreamRepository
    @Autowired private lateinit var gameRepository: GameRepository
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var tournamentRepository: TournamentRepository

    @Test
    fun test() {
        val newGame = createGame() // create new game to link new stream to

        val twitchResponse = createTwitchResponse()
        `when`(twitchService.requestVideos(anyObject())).thenReturn(twitchResponse)

        twitchScheduler.schedule()

        val streams = streamRepository.findAll()
        assertThat(streams).hasSize(3)
        assertThat(streams.map { it.id })
            .containsExactlyInAnyOrder("804444415", "43121370", "12678349")

        // existing without associated game
        assertThat(streams.find { it.id == "804444415" }!!.game).isNull()

        // existing with associated game
        assertThat(streams.find { it.id == "43121370" }!!.game!!.id).isEqualTo(1081)

        // new game that gets linked
        assertThat(streams.find { it.id == "12678349" }!!.game).isEqualTo(newGame)
    }

    private fun createGame(): Game {
        val kayz = userRepository.getOne(10)
        val khamski = userRepository.getOne(5)
        val tournament = tournamentRepository.getOne(1)
        val game = Game(homeUser = kayz, awayUser = khamski, tournament = tournament)
        return gameRepository.save(game)
    }

    private fun createTwitchResponse(): List<TwitchVideoDto> {
        var twitchVideoDtos = mutableListOf<TwitchVideoDto>()
        val streams = streamRepository.findAll()
        val randomChannel = streams[3].channel

        val existingWithoutAssociatedGame = streams.find { it.id == "804444415" }
        assertThat(existingWithoutAssociatedGame).isNotNull()
        assertThat(existingWithoutAssociatedGame!!.game).isNull()
        twitchVideoDtos.add(toTwitchVideoDto(existingWithoutAssociatedGame))

        val existingWithAssociatedGame = streams.find { it.id == "43121370" }
        assertThat(existingWithAssociatedGame).isNotNull()
        assertThat(existingWithAssociatedGame!!.game).isNotNull()
        assertThat(existingWithAssociatedGame.game!!.id).isEqualTo(1081)
        twitchVideoDtos.add(toTwitchVideoDto(existingWithAssociatedGame))

        // one entirely new stream
        twitchVideoDtos.add(createTwitchVideoDto("12678349", randomChannel.id, "CWT Kayz vs Khamski"))

        return twitchVideoDtos
    }

    private fun toTwitchVideoDto(stream: Stream): TwitchVideoDto =
            createTwitchVideoDto(stream.id, stream.userId!!)

    private fun createTwitchVideoDto(id: String, userId: String, title: String = "default title"): TwitchVideoDto {
        val twitchVideoDto = mock(TwitchVideoDto::class.java)
        `when`(twitchVideoDto.id).thenReturn(id)
        `when`(twitchVideoDto.userId).thenReturn(userId)
        `when`(twitchVideoDto.title).thenReturn(title)
        `when`(twitchVideoDto.hasCwtInTitle()).thenReturn(true)
        return twitchVideoDto
    }
}

