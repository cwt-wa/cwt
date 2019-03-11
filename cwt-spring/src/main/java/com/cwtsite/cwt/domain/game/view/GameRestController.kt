package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.FileValidator
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.game.view.model.GameTechWinDto
import com.cwtsite.cwt.domain.game.view.model.ReportDto
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.entity.Comment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/game")
class GameRestController @Autowired
constructor(private val gameService: GameService, private val userService: UserService, private val messageService: MessageService,
            private val authService: AuthService) {

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable("id") id: Long): ResponseEntity<GameDetailDto> {
        return gameService.findById(id)
                .map { body -> ResponseEntity.ok(GameDetailDto.toDto(body)) }
                .orElseGet { ResponseEntity.status(HttpStatus.NOT_FOUND).build() }
    }

    @RequestMapping("", method = [RequestMethod.POST])
    fun reportGameWithoutReplay(@RequestBody reportDto: ReportDto, request: HttpServletRequest): ResponseEntity<GameCreationDto> {
        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName)).id != reportDto.user) {
            throw RestException("Please report your own games.", HttpStatus.FORBIDDEN, null);
        }

        val reportedGame = gameService.reportGame(
                reportDto.user!!, reportDto.opponent!!,
                reportDto.scoreOfUser!!.toInt(), reportDto.scoreOfOpponent!!.toInt())
        return ResponseEntity.ok(GameCreationDto.toDto(reportedGame))
    }

    @RequestMapping("", method = [RequestMethod.POST], consumes = ["multipart/form-data"])
    @Throws(IOException::class)
    fun reportGameWithReplayFile(
            @RequestParam("replay") replay: MultipartFile,
            @RequestParam("score-home") scoreHome: Int,
            @RequestParam("score-away") scoreAway: Int,
            @RequestParam("home-user") homeUser: Long,
            @RequestParam("away-user") awayUser: Long,
            request: HttpServletRequest): ResponseEntity<GameCreationDto> {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser.id != homeUser && authUser.id != awayUser) throw RestException("Please report your own games.", HttpStatus.FORBIDDEN, null)

        val game: Game
        try {
            game = gameService.reportGame(homeUser, awayUser, scoreHome, scoreAway, replay)
            GlobalScope.launch {
                messageService.publishNews(
                        MessageNewsType.REPORT, authUser, game.id,
                        game.homeUser!!.username, game.awayUser!!.username,
                        scoreHome, scoreAway)
            }
        } catch (e: GameService.InvalidOpponentException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        } catch (e: GameService.InvalidScoreException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        } catch (e: FileValidator.UploadSecurityException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        } catch (e: FileValidator.FileEmptyException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        } catch (e: FileValidator.IllegalFileContentTypeException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        } catch (e: FileValidator.FileTooLargeException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        } catch (e: FileValidator.IllegalFileExtension) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        }

        return ResponseEntity.ok(GameCreationDto.toDto(game))
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
    fun queryGamesPaged(pageDto: PageDto<Game>): ResponseEntity<PageDto<GameDetailDto>> {
        return ResponseEntity.ok(PageDto.toDto(
                gameService.findPaginated(
                        pageDto.start, pageDto.size,
                        pageDto.asSortWithFallback(Sort.Direction.DESC, "created")).map { GameDetailDto.toDto(it) },
                Arrays.asList("created,Creation", "ratingsSize,Ratings", "commentsSize,Comments")))
    }

    @RequestMapping("/{id}/rating", method = [RequestMethod.POST])
    fun rateGame(@PathVariable("id") id: Long, @RequestBody rating: RatingDto, request: HttpServletRequest): Rating {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser.id != rating.user) {
            throw RestException("Please rate as yourself.", HttpStatus.FORBIDDEN, null);
        }
        val persistedRating = gameService.rateGame(id, rating.user, rating.type)

        GlobalScope.launch {
            messageService.publishNews(
                    MessageNewsType.RATING, authUser, persistedRating.game.id,
                    persistedRating.game.homeUser!!.username, persistedRating.game.awayUser!!.username,
                    persistedRating.game.scoreHome, persistedRating.game.scoreAway, rating.type.name.toLowerCase())
        }

        return persistedRating
    }

    @RequestMapping("/{id}/comment", method = [RequestMethod.POST])
    fun commentGame(@PathVariable("id") id: Long, @RequestBody comment: CommentDto, request: HttpServletRequest): Comment {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser.id != comment.user) {
            throw RestException("Please comment as yourself.", HttpStatus.FORBIDDEN, null);
        }

        val persistedComment = gameService.commentGame(id, comment.user, comment.body)

        GlobalScope.launch {
            messageService.publishNews(
                    MessageNewsType.COMMENT, authUser, persistedComment.game.id,
                    persistedComment.game.homeUser!!.username, persistedComment.game.awayUser!!.username,
                    persistedComment.game.scoreHome, persistedComment.game.scoreAway)
        }

        return persistedComment
    }

    @RequestMapping("/tech-win", method = [RequestMethod.POST])
    fun addTechWin(@RequestBody dto: GameTechWinDto): ResponseEntity<GameCreationDto> {
        val users = userService.findByIds(dto.winner, dto.loser)
        return ResponseEntity.ok(GameCreationDto.toDto(
                gameService.addTechWin(users.find { it.id == dto.winner }!!, users.find { it.id == dto.loser }!!)))
    }
}