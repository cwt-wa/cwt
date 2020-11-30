package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.ClockInstance
import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.core.event.SseEmitterWrapper
import com.cwtsite.cwt.core.event.stats.GameStatSubscription
import com.cwtsite.cwt.core.event.stats.GameStatsEventListener
import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.BetCreationDto
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.game.view.model.RatingCreationDto
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import com.cwtsite.cwt.test.MockitoUtils.safeEq
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import java.util.*
import java.sql.Timestamp
import javax.servlet.http.HttpServletRequest
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class GameRestControllerTest {

    @InjectMocks private lateinit var cut: GameRestController
    @Mock private lateinit var gameService: GameService
    @Mock private lateinit var messageService: MessageService
    @Mock private lateinit var treeService: TreeService
    @Mock private lateinit var clockInstance: ClockInstance
    @Mock private lateinit var sseEmitterFactory: SseEmitterFactory
    @Mock private lateinit var gameStatsEventListener: GameStatsEventListener
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var authService: AuthService
    @Mock private lateinit var userService: UserService
    @Mock private lateinit var streamService: StreamService
    @Captor private lateinit var subCaptor: ArgumentCaptor<GameStatSubscription>
    @Captor private lateinit var onCompletionCaptor: ArgumentCaptor<() -> Unit>

    @Test
    fun `queryGamesPaged without users`() {
        val game = EntityDefaults.game()
        val pageDto = mock(PageDto::class.java) as PageDto<Game>
        `when`(pageDto.size).thenReturn(10)
        `when`(pageDto.start).thenReturn(1)
        `when`(pageDto.asSortWithFallback(anyObject(), anyString())).thenCallRealMethod()
        val result = PageImpl<Game>(listOf(game), PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "reportedAt")), 1)
        `when`(gameService.findPaginatedPlayedGames(anyInt(), anyInt(), anyObject())).thenReturn(result)
        assertThat(cut.queryGamesPaged(pageDto, null)).extracting { it.body!! }.satisfies {
            assertThat(it.content).containsExactly(GameDetailDto.toDto(game))
            assertThat(it.size).isEqualTo(result.size)
            assertThat(it.start).isEqualTo(pageDto.start)
            assertThat(it.sortables).containsExactlyInAnyOrder(
                    "reportedAt,Reported at", "ratingsSize,Ratings", "commentsSize,Comments")
            assertThat(it.sortBy).isEqualTo("reportedAt")
            assertThat(it.isSortAscending).isFalse()
        }
    }

    @Test
    fun `queryGamesPaged with users`() {
        val game = EntityDefaults.game()
        val pageDto = mock(PageDto::class.java) as PageDto<Game>
        `when`(pageDto.size).thenReturn(10)
        `when`(pageDto.start).thenReturn(1)
        `when`(pageDto.asSortWithFallback(anyObject(), anyString())).thenCallRealMethod()
        `when`(userService.getById(game.homeUser!!.id!!)).thenReturn(Optional.of(game.homeUser!!))
        `when`(userService.getById(game.awayUser!!.id!!)).thenReturn(Optional.of(game.awayUser!!))
        val result = PageImpl<Game>(listOf(game), PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "reportedAt")), 2)
        `when`(gameService.findGameOfUsers(anyInt(), anyInt(), anyObject(), safeEq(game.homeUser!!), safeEq(game.awayUser!!)))
                .thenReturn(result)
        assertThat(cut.queryGamesPaged(pageDto, listOf(game.homeUser!!.id!!, game.awayUser!!.id!!))).extracting { it.body!! }.satisfies {
            assertThat(it.content).containsExactly(GameDetailDto.toDto(game))
            assertThat(it.size).isEqualTo(result.size)
            assertThat(it.start).isEqualTo(pageDto.start)
            assertThat(it.sortables).containsExactlyInAnyOrder(
                    "reportedAt,Reported at", "ratingsSize,Ratings", "commentsSize,Comments")
            assertThat(it.sortBy).isEqualTo("reportedAt")
            assertThat(it.isSortAscending).isFalse()
        }

    }

    @Test
    fun `queryGamesPaged all the wrong number of users`() {
        val pageDto = PageDto.empty<Game>()
        val exception1 = assertThrows<RestException> {
            cut.queryGamesPaged(pageDto, listOf(EntityDefaults.user().id!!))
        }
        assertThat(exception1.status).isEqualTo(HttpStatus.BAD_REQUEST)
        val exception2 = assertThrows<RestException> {
            cut.queryGamesPaged(
                    pageDto,
                    listOf(EntityDefaults.user().id!!, EntityDefaults.user().id!!, EntityDefaults.user().id!!))
        }
        assertThat(exception2.status).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun placeBetOnGame_works() {
        val game = EntityDefaults.game(scoreHome = null, scoreAway = null, playoff = PlayoffGame(1, 1, 1))
        val user = EntityDefaults.user()
        val request = mock(HttpServletRequest::class.java)
        val dto = BetCreationDto(user.id!!, game.id!!, false)
        val bet = Bet(id = 1, user = user, game = game, betOnHome = dto.betOnHome)
        `when`(gameService.findById(anyLong())).thenReturn(Optional.of(game))
        `when`(userService.getById(anyLong())).thenReturn(Optional.of(user))
        `when`(authService.authUser(request)).thenReturn(user)
        `when`(gameService.placeBet(anyObject(), anyObject(), anyBoolean())).thenReturn(bet)
        cut.placeBetOnGame(game.id!!, dto, request)
        verify(gameService).placeBet(game, user, dto.betOnHome)
    }

    @Test
    fun placeBetOnGame_alreadyPlayed() {
        val game = EntityDefaults.game(playoff = PlayoffGame(1, 1, 1))
        val user = EntityDefaults.user()
        val request = mock(HttpServletRequest::class.java)
        val dto = BetCreationDto(user.id!!, game.id!!, false)
        `when`(gameService.findById(anyLong())).thenReturn(Optional.of(game))
        `when`(userService.getById(anyLong())).thenReturn(Optional.of(user))
        `when`(authService.authUser(request)).thenReturn(user)
        assertThatThrownBy { cut.placeBetOnGame(game.id!!, dto, request) }
                .isExactlyInstanceOf(RestException::class.java)
                .satisfies { assertThat((it as RestException).status).isEqualTo(HttpStatus.BAD_REQUEST) }
    }

    @Test
    fun placeBetOnGame_impersonate() {
        val game = EntityDefaults.game(playoff = PlayoffGame(1, 1, 1))
        val user = EntityDefaults.user()
        val request = mock(HttpServletRequest::class.java)
        val dto = BetCreationDto(2, game.id!!, false)
        `when`(gameService.findById(anyLong())).thenReturn(Optional.of(game))
        `when`(userService.getById(anyLong())).thenReturn(Optional.of(user))
        `when`(authService.authUser(request)).thenReturn(user)
        assertThatThrownBy { cut.placeBetOnGame(game.id!!, dto, request) }
                .isExactlyInstanceOf(RestException::class.java)
                .satisfies { assertThat((it as RestException).status).isEqualTo(HttpStatus.FORBIDDEN) }
    }

    @Test
    fun listenToStats() {
        val game = EntityDefaults.game()
        game.replayQuantity = 3
        val sseEmitter = mock(SseEmitterWrapper::class.java)
        `when`(sseEmitterFactory.createInstance(anyLong())).thenReturn(sseEmitter)
        cut.listenToStats(game.id!!, game.replayQuantity!!)
        verify(gameStatsEventListener).subscribe(MockitoUtils.capture(subCaptor))
        verify(sseEmitter).onCompletion(MockitoUtils.capture(onCompletionCaptor))
        subCaptor.value.callback("stats1")
        subCaptor.value.callback("stats2")
        subCaptor.value.callback("stats3")
        verify(sseEmitter).send("EVENT", "stats1")
        verify(sseEmitter).send("EVENT", "stats2")
        verify(sseEmitter).send("EVENT", "stats3")
        verify(sseEmitter).send("DONE", "DONE")
        verify(sseEmitter).complete()
        onCompletionCaptor.value.invoke()
        verify(gameStatsEventListener).unsubscribe(subCaptor.value)
    }

    @Test
    fun `place rating`() {
        val game = EntityDefaults.game()
        val user = EntityDefaults.user()
        val rating = RatingCreationDto(user = user.id!!, type = RatingType.LIGHTSIDE)
        `when`(authService.authUser(anyObject())).thenReturn(user)
        `when`(gameService.findById(game.id!!)).thenReturn(Optional.of(game))
        `when`(gameService.findRating(user, game, rating.type, rating.type.opposite()))
                .thenReturn(emptyList())
        val savedRating = Rating(
                id = 1234231421,
                game = game,
                type = rating.type,
                user = user,
                modified = Timestamp(1606723883495))
        `when`(gameService.rateGame(user, game, rating.type)).thenReturn(savedRating)
        val actual = cut.rateGame(user.id!!, rating, mock(HttpServletRequest::class.java))
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(actual.body).isEqualTo(RatingDto.toDto(savedRating))
    }

    @Test
    fun `update rating`() {
        val game = EntityDefaults.game()
        val user = EntityDefaults.user()
        val existingRatings = listOf(EntityDefaults.rating())
        val rating = RatingCreationDto(user = user.id!!, type = RatingType.LIGHTSIDE)
        `when`(authService.authUser(anyObject())).thenReturn(user)
        `when`(gameService.findById(game.id!!)).thenReturn(Optional.of(game))
        `when`(gameService.findRating(user, game, rating.type, rating.type.opposite()))
                .thenReturn(existingRatings)
        `when`(gameService.updateRating(existingRatings[0], rating.type)).thenAnswer { it.getArgument(0) }
        val actual = cut.rateGame(user.id!!, rating, mock(HttpServletRequest::class.java))
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(actual.body).isEqualTo(RatingDto.toDto(existingRatings[0]))
    }

    @Test
    fun `deletes existing ratings when more than 1`() {
        val game = EntityDefaults.game()
        val user = EntityDefaults.user()
        val existingRatings = listOf(EntityDefaults.rating(), EntityDefaults.rating(id = 2))
        val rating = RatingCreationDto(user = user.id!!, type = RatingType.LIGHTSIDE)
        `when`(authService.authUser(anyObject())).thenReturn(user)
        `when`(gameService.findById(game.id!!)).thenReturn(Optional.of(game))
        `when`(gameService.findRating(user, game, rating.type, rating.type.opposite()))
                .thenReturn(existingRatings)
        val savedRating = Rating(
                id = 1234231421,
                game = game,
                type = rating.type,
                user = user,
                modified = Timestamp(1606723883495))
        `when`(gameService.rateGame(user, game, rating.type)).thenReturn(savedRating)
        val actual = cut.rateGame(user.id!!, rating, mock(HttpServletRequest::class.java))
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(actual.body).isEqualTo(RatingDto.toDto(savedRating))
        verify(gameService).deleteRatings(existingRatings.toList())
    }
}
