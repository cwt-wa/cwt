package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.ClockInstance
import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.core.event.stats.GameStatSubscription
import com.cwtsite.cwt.core.event.stats.GameStatsEventListener
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.*
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.tournament.view.model.PlayoffTreeBetDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.entity.Comment
import org.json.JSONArray
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional
import javax.ws.rs.Produces
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.schedule

@RestController
@RequestMapping("api/game")
class GameRestController @Autowired
constructor(private val gameService: GameService, private val userService: UserService,
            private val authService: AuthService,
            private val clockInstance: ClockInstance,
            private val sseEmitterFactory: SseEmitterFactory,
            private val gameStatsEventListener: GameStatsEventListener,
            private val tournamentService: TournamentService,
            private val streamService: StreamService) {

    @Value("\${stats-sse-timeout:#{180000}}") // default of 3 minutes
    private var statsSseTimeout: Long? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{id}/stats-listen", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun listenToStats(@PathVariable("id") gameId: Long): ResponseBodyEmitter {
        val game = gameService
                .findById(gameId)
                .orElseThrow { RestException("Game not found", HttpStatus.NOT_FOUND, null) }

        val emitter = sseEmitterFactory.createInstance()
        var isComplete = false
        var emissions = 0
        val complete: () -> Unit = {
            if (!isComplete) {
                emitter.send(SseEmitter.event().data("DONE").name("DONE"))
                emitter.complete()
                isComplete = true
            }
        }

        if (game.techWin) {
            complete()
            return emitter
        }

        val emit = { data: String ->
            emitter.send(SseEmitter.event().data(data).name("EVENT"))
            logger.info("Emitted game stats (length: ${data.length}) event to game $gameId")
            emissions += 1
            if (emissions == game.replayQuantity) {
                complete()
            }
        }

        val keepAliveTimer = fixedRateTimer(period = 10000) {
            emitter.send(SseEmitter.event().data("KEEPALIVE").name("KEEPALIVE"))
        }

        val timePassedSinceReport = clockInstance.now.minusMillis(game.reportedAt!!.time).toEpochMilli()
        if (emissions != game.replayQuantity && timePassedSinceReport < statsSseTimeout!!) {
            Timer(Thread.currentThread().name, false).schedule(statsSseTimeout!!) { emitter.complete() }
            val subscription = GameStatSubscription(gameId, emit)
            gameStatsEventListener.subscribe(subscription)
            emitter.onCompletion {
                gameStatsEventListener.unsubscribe(subscription)
                keepAliveTimer.cancel()
            }
            emitter.onTimeout {
                gameStatsEventListener.unsubscribe(subscription)
                keepAliveTimer.cancel()
            }
        } else {
            complete()
        }

        return emitter
    }

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable("id") id: Long): ResponseEntity<GameDetailDto> {
        return gameService.findById(id)
                .map { ResponseEntity.ok(GameDetailDto.toDto(it)) }
                .orElseThrow { RestException("Game not found", HttpStatus.NOT_FOUND, null) }
    }

    @GetMapping("/{id}/stream")
    fun getStreamsForGame(@PathVariable("id") id: Long): ResponseEntity<List<StreamDto>> {
        val game = gameService.findById(id)
                .orElseThrow { RestException("Game $id not found", HttpStatus.NOT_FOUND, null) }
        return ResponseEntity.ok(streamService.findStreams(game).map { StreamDto.toDto(it) })
    }

    @RequestMapping("", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun reportGameWithoutReplay(@RequestBody reportDto: ReportDto,
                                request: HttpServletRequest): ResponseEntity<GameCreationDto> {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser!!.id != reportDto.user) {
            throw RestException("Please report your own games.", HttpStatus.FORBIDDEN, null)
        }

        val reportedGame = gameService.reportGame(
                reportDto.user!!, reportDto.opponent!!,
                reportDto.scoreOfUser!!.toInt(), reportDto.scoreOfOpponent!!.toInt())

        return ResponseEntity.ok(GameCreationDto.toDto(reportedGame))
    }

    @RequestMapping("/{gameId}/replay", method = [RequestMethod.GET])
    @Throws(IOException::class)
    fun download(@PathVariable("gameId") gameId: Long): ResponseEntity<Resource> {
        val game = gameService.findById(gameId)
                .orElseThrow { RestException("Game $gameId not found", HttpStatus.NOT_FOUND, null) }

        if (game.replay == null) {
            throw RestException("There's no replay file for this game.", HttpStatus.NOT_FOUND, null)
        }

        val resource = ByteArrayResource(game.replay!!.file)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + gameService.createReplayFileName(game))
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(game.replay!!.mediaType))
                .body(resource)
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun queryGamesPaged(pageDto: PageDto<Game>,
                        @RequestParam("user", required = true) users: List<Long>?): ResponseEntity<PageDto<GameDetailDto>> {
        val games = when (users?.size ?: 0) {
            0 -> {
                gameService.findPaginatedPlayedGames(
                        pageDto.start, pageDto.size,
                        pageDto.asSortWithFallback(Sort.Direction.DESC, "reportedAt"))
            }
            1 -> {
                val user = userService.getById(users!![0])
                        .orElseThrow { RestException("user ${users[0]} does not exist.", HttpStatus.BAD_REQUEST, null) }
                gameService.findGameOfUser(
                        pageDto.start, pageDto.size,
                        pageDto.asSortWithFallback(Sort.Direction.DESC, "reportedAt"),
                        user)
            }
            2 -> {
                val user1 = userService.getById(users!![0])
                        .orElseThrow { RestException("User ${users[0]} does not exist", HttpStatus.NOT_FOUND, null) }
                val user2 = userService.getById(users[1])
                        .orElseThrow { RestException("User ${users[1]} does not exist", HttpStatus.NOT_FOUND, null) }
                gameService.findGameOfUsers(
                        pageDto.start, pageDto.size,
                        pageDto.asSortWithFallback(Sort.Direction.DESC, "reportedAt"),
                        user1, user2)
            }
            else -> {
                throw RestException("Two users must be supplied", HttpStatus.BAD_REQUEST, null)
            }
        }
        return ResponseEntity.ok(PageDto.toDto(
                games.map { GameDetailDto.toDto(it) },
                listOf("reportedAt,Reported at", "ratingsSize,Ratings", "commentsSize,Comments")))
    }

    @RequestMapping("/{id}/rating", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun rateGame(@PathVariable("id") id: Long, @RequestBody rating: RatingDto, request: HttpServletRequest): Rating {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser!!.id != rating.user) {
            throw RestException("Please rate as yourself.", HttpStatus.FORBIDDEN, null)
        }
        val persistedRating = gameService.rateGame(id, rating.user, rating.type)

        return persistedRating
    }

    @RequestMapping("/{id}/comment", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun commentGame(@PathVariable("id") id: Long, @RequestBody comment: CommentDto, request: HttpServletRequest): Comment {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser!!.id != comment.user) {
            throw RestException("Please comment as yourself.", HttpStatus.FORBIDDEN, null)
        }

        val persistedComment = gameService.commentGame(id, comment.user, comment.body)

        return persistedComment
    }

    @RequestMapping("/tech-win", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun addTechWin(@RequestBody dto: GameTechWinDto, request: HttpServletRequest): ResponseEntity<GameCreationDto> {
        tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        val reporter = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
                ?: throw RestException("Unauthorized.", HttpStatus.UNAUTHORIZED, null)
        val users = userService.findByIds(dto.winner, dto.loser)
        val winningUser = users.find { it.id == dto.winner }
                ?: throw RestException("Winning user not found.", HttpStatus.BAD_REQUEST, null)
        val losingUser = users.find { it.id == dto.loser }
                ?: throw RestException("Losing user not found.", HttpStatus.BAD_REQUEST, null)
        return ResponseEntity.ok(GameCreationDto.toDto(gameService.addTechWin(winningUser, losingUser, reporter)))
    }

    @RequestMapping("/{id}/bet", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun placeBetOnGame(@PathVariable("id") id: Long, @RequestBody dto: BetCreationDto, request: HttpServletRequest): ResponseEntity<BetDto> {
        val game = gameService.findById(id).orElseThrow { RestException("Game $id not found", HttpStatus.NOT_FOUND, null) }
        val user = userService.getById(dto.user).orElseThrow { RestException("User ${dto.user} not found", HttpStatus.NOT_FOUND, null) }

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))!!.id != dto.user) {
            throw RestException("You must not impersonate your bet.", HttpStatus.FORBIDDEN, null)
        }

        if (game.wasPlayed()) {
            throw RestException("The game has already been played.", HttpStatus.BAD_REQUEST, null)
        }

        return ResponseEntity.ok(BetDto.toDto(gameService.placeBet(game, user, dto.betOnHome)))
    }

    @RequestMapping("/{id}/bets", method = [RequestMethod.GET])
    fun queryBetsForGame(@PathVariable("id") id: Long): ResponseEntity<List<PlayoffTreeBetDto>> = ResponseEntity.ok(
            gameService.findBetsByGame(gameService.findById(id).orElseThrow { RestException("Game $id not found", HttpStatus.NOT_FOUND, null) })
                    .map { PlayoffTreeBetDto.toDto(it) })

    @RequestMapping("/{id}/void", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_ADMIN)
    @Transactional
    fun voidGame(@PathVariable("id") id: Long, request: HttpServletRequest): ResponseEntity<Game> {
        val voidedGame = try {
            gameService.voidGame(gameService.findById(id)
                    .orElseThrow { RestException("Game not found", HttpStatus.NOT_FOUND, null) })
        } catch (e: PlayoffService.PlayoffGameNotVoidableException) {
            throw RestException("The game is not voidable.", HttpStatus.BAD_REQUEST, e)
        } catch (e: IllegalStateException) {
            throw RestException(e.message ?: "Could not void game.", HttpStatus.BAD_REQUEST, e)
        }

        return ResponseEntity.ok(voidedGame)
    }

    @GetMapping("/{gameId}/stats")
    @Produces("application/json")
    fun retrieveGameStats(@PathVariable gameId: Long): ResponseEntity<String> =
            ResponseEntity.ok(gameService.findGameStats(
                    gameService.findById(gameId)
                            .orElseThrow { RestException("Game not found", HttpStatus.NOT_FOUND, null) }))
}
