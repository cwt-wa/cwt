package com.cwtsite.cwt.core.news

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.schedule.service.ScheduleService
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import com.cwtsite.cwt.test.EntityDefaults
import org.mockito.Mockito.*
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory
import java.sql.Timestamp
import kotlin.test.Test


class NewsAspectTest {

    private val messageService = mock(MessageService::class.java)
    private val userRepository = mock(UserRepository::class.java)

    private val author = User(username = "Zemke", email = "zemke@example.com", id = 1)

    @Test
    fun gameReport() {
        val awayUser = User(username = "Rafka", email = "rafka@example.com", id = 2)
        val game = Game(
                id = 1,
                homeUser = author, awayUser = awayUser, scoreHome = 3, scoreAway = 0,
                tournament = EntityDefaults.tournament())
        val target = mock(GameService::class.java)
        `when`(target.reportGame(author.id!!, awayUser.id!!, game.scoreHome!!, game.scoreAway!!)).thenReturn(game)
        val proxy = createProxy<GameService>(target, author)
        proxy.reportGame(author.id!!, awayUser.id!!, game.scoreHome!!, game.scoreAway!!)
        verify(messageService).publishNews(
                MessageNewsType.REPORT, author, game.id!!, author.username,
                awayUser.username, game.scoreHome, game.scoreAway)
    }

    @Test
    fun voidGame() {
        val awayUser = User(username = "Rafka", email = "rafka@example.com", id = 2)
        val game = Game(
                id = 1,
                homeUser = author, awayUser = awayUser, scoreHome = 3, scoreAway = 0,
                tournament = EntityDefaults.tournament())
        val voidedGame = game.copy(voided = true)
        val target = mock(GameService::class.java)
        `when`(target.voidGame(game)).thenReturn(voidedGame)
        val proxy = createProxy<GameService>(target, author)
        proxy.voidGame(game)
        verify(messageService).publishNews(
                MessageNewsType.VOIDED, author, game.id!!, author.username,
                awayUser.username, game.scoreHome, game.scoreAway)
    }

    @Test
    fun commentGame() {
        val awayUser = User(username = "Rafka", email = "rafka@example.com", id = 2)
        val game = Game(
                id = 1,
                homeUser = author, awayUser = awayUser, scoreHome = 3, scoreAway = 0,
                tournament = EntityDefaults.tournament())
        val target = mock(GameService::class.java)
        val comment = Comment(body = "Interesting", game = game, author = author)
        `when`(target.commentGame(game.id!!, author.id!!, comment.body!!)).thenReturn(comment)
        val proxy = createProxy<GameService>(target, author)
        proxy.commentGame(game.id!!, author.id!!, comment.body!!)
        verify(messageService).publishNews(
                MessageNewsType.COMMENT, author, game.id!!, author.username,
                awayUser.username, game.scoreHome, game.scoreAway)
    }

    @Test
    fun rateGame() {
        val awayUser = User(username = "Rafka", email = "rafka@example.com", id = 2)
        val game = Game(
                id = 1,
                homeUser = author, awayUser = awayUser, scoreHome = 3, scoreAway = 0,
                tournament = EntityDefaults.tournament())
        val target = mock(GameService::class.java)
        val rating = Rating(RatingType.LIKE, author, game)
        `when`(target.rateGame(game.id!!, author.id!!, rating.type)).thenReturn(rating)
        val proxy = createProxy<GameService>(target, author)
        proxy.rateGame(game.id!!, author.id!!, rating.type)
        verify(messageService).publishNews(
                MessageNewsType.RATING, author, game.id!!, author.username,
                awayUser.username, game.scoreHome, game.scoreAway, rating.type.name.toLowerCase())
    }

    @Test
    fun streamGame() {
        val game = EntityDefaults.game()
        val stream = EntityDefaults.stream()
        val target = mock(StreamService::class.java)
        `when`(target.associateGame(stream, game)).thenReturn(stream.copy(game = game))
        val proxy = createProxy<StreamService>(target, author)
        proxy.associateGame(stream, game)
        verify(messageService).publishNews(
        MessageNewsType.STREAM, stream.channel.user, stream.id, stream.title)
    }
    /*
    <ng-container *ngSwitchCase="'removeStream'">cancelled the live stream for</ng-container>
    <ng-container *ngSwitchCase="'scheduleStream'">scheduled a live stream for</ng-container>
    <ng-container *ngSwitchCase="'createSchedule'">scheduled a game</ng-container>
    <ng-container *ngSwitchCase="'cancelSchedule'">cancelled the game</ng-container>
     */

    @Test
    fun removeStream() {
        val homeUser = author
        val awayUser = EntityDefaults.user(id = 4, username = "Rafka")
        val channel = EntityDefaults.channel()
        val schedule = EntityDefaults.schedule(
                homeUser = homeUser,
                awayUser = awayUser,
                streams = mutableSetOf(channel))
        val target = mock(ScheduleService::class.java)
        `when`(target.removeStream(schedule, channel))
                .thenReturn(schedule.copy(streams = mutableSetOf()))
        val proxy = createProxy<ScheduleService>(target, author)
        proxy.removeStream(schedule, channel)
        verify(messageService).publishNews(
                MessageNewsType.SCHEDULE, channel.user,
                "removeStream",
                homeUser.username, awayUser.username,
                schedule.appointment)
    }

    @Test
    fun scheduleStream() {
        val homeUser = author
        val awayUser = EntityDefaults.user(id = 4, username = "Rafka")
        val channel = EntityDefaults.channel()
        val schedule = EntityDefaults.schedule(
                homeUser = homeUser, awayUser = awayUser)
        val target = mock(ScheduleService::class.java)
        `when`(target.scheduleStream(schedule, channel))
                .thenReturn(schedule.copy(streams = mutableSetOf(channel)))
        val proxy = createProxy<ScheduleService>(target, author)
        proxy.scheduleStream(schedule, channel)
        verify(messageService).publishNews(
                MessageNewsType.SCHEDULE, channel.user,
                "scheduleStream",
                homeUser.username, awayUser.username,
                schedule.appointment)
    }

    @Test
    fun createSchedule() {
        val homeUser = author
        val awayUser = EntityDefaults.user(id = 4, username = "Rafka")
        val appointment = Timestamp(1577813200000)
        val schedule = EntityDefaults.schedule(
                homeUser = homeUser, awayUser = awayUser)
        val target = mock(ScheduleService::class.java)
        `when`(target.createSchedule(homeUser, awayUser, appointment))
                .thenReturn(schedule)
        val proxy = createProxy<ScheduleService>(target, author)
        proxy.createSchedule(homeUser, awayUser, appointment)
        verify(messageService).publishNews(
                MessageNewsType.SCHEDULE, author,
                "createSchedule",
                homeUser.username, awayUser.username,
                schedule.appointment)
    }

    @Test
    fun cancelSchedule() {
        val homeUser = author
        val awayUser = EntityDefaults.user(id = 4, username = "Rafka")
        val schedule = EntityDefaults.schedule(
                homeUser = homeUser, awayUser = awayUser)
        val target = mock(ScheduleService::class.java)
        `when`(target.cancelSchedule(schedule))
                .thenReturn(schedule)
        val proxy = createProxy<ScheduleService>(target, author)
        proxy.cancelSchedule(schedule)
        verify(messageService).publishNews(
                MessageNewsType.SCHEDULE, author,
                "cancelSchedule",
                homeUser.username, awayUser.username,
                schedule.appointment)
    }

    @Test
    fun deleteSchedule() {
        val homeUser = EntityDefaults.user(id = 5, username = "PavelB")
        val awayUser = EntityDefaults.user(id = 4, username = "Rafka")
        val schedule = EntityDefaults.schedule(
                homeUser = homeUser, awayUser = awayUser)
        val target = mock(ScheduleService::class.java)
        `when`(target.deleteSchedule(schedule))
                .thenReturn(schedule)
        val proxy = createProxy<ScheduleService>(target, author)
        proxy.deleteSchedule(schedule)
        verifyNoInteractions(messageService)
    }

    private fun <T> createProxy(target: T, author: User): T {
        val factory = AspectJProxyFactory(target)
        `when`(userRepository.findByUsername(author.username)).thenReturn(author)
        val securityCtx = mock(SecurityContextHolderFacade::class.java)
        `when`(securityCtx.authenticationName).thenReturn(author.username)
        val aspect = NewsAspect(messageService, securityCtx, userRepository)
        factory.addAspect(aspect)
        return factory.getProxy()
    }
}
