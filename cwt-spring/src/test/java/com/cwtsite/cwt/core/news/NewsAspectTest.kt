package com.cwtsite.cwt.core.news

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import com.cwtsite.cwt.test.EntityDefaults
import org.mockito.Mockito.*
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory
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
