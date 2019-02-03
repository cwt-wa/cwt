package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.FileValidator
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.game.view.model.ReportDto
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.entity.Comment
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

@RestController
@RequestMapping("api/game")
class GameRestController @Autowired
constructor(private val gameService: GameService, private val userService: UserService, private val tournamentService: TournamentService) {

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getGame(@PathVariable("id") id: Long): ResponseEntity<GameDetailDto> {
        return gameService.get(id)
                .map { body -> ResponseEntity.ok(GameDetailDto.toDto(body)) }
                .orElseGet { ResponseEntity.status(HttpStatus.NOT_FOUND).build() }
    }

    @RequestMapping("", method = [RequestMethod.POST])
    fun reportGameWithoutReplay(@RequestBody reportDto: ReportDto): ResponseEntity<GameCreationDto> {
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
            @RequestParam("away-user") awayUser: Long): ResponseEntity<GameCreationDto> {
        val game: Game
        try {
            game = gameService.reportGame(homeUser, awayUser, scoreHome, scoreAway, replay)
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
        val game = gameService.get(gameId)
                .orElseThrow { RestException("Game $gameId not found", HttpStatus.NOT_FOUND, null) }

        if (game.replay == null) {
            throw RestException("There's no replay file for this game.", HttpStatus.NOT_FOUND, null)
        }

        val resource = ByteArrayResource(game.replay.file)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + gameService.createReplayFileName(game))
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(game.replay.mediaType))
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
    fun rateGame(@PathVariable("id") id: Long, @RequestBody rating: RatingDto): Rating {
        return gameService.rateGame(id, rating.user, rating.type)
    }

    @RequestMapping("/{id}/comment", method = [RequestMethod.POST])
    fun commentGame(@PathVariable("id") id: Long, @RequestBody comment: CommentDto): Comment {
        return gameService.commentGame(id, comment.user, comment.body)
    }
}
