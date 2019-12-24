package com.cwtsite.cwt.controller

import com.cwtsite.cwt.core.FileValidator
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.AuthService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("api/binary")
class BinaryRestController {

    @Value("\${binary-data-store")
    private var binaryDataStoreEndpoint: String? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var messageService: MessageService

    @GetMapping("user/{userId}/photo")
    fun getUserPhoto(@PathVariable userId: Long): ResponseEntity<ByteArray> {
        assertBinaryDataStoreEndpoint()

        val response = khttp.get(url = "${binaryDataStoreEndpoint}user/$userId/photo")

        if (response.statusCode != 200) {
            if (response.statusCode == 404) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
            logger.error("HTTP ${response.statusCode}: ${response.content.toString(Charset.defaultCharset())}")
            throw RestException("Ew, something went wrong.", HttpStatus.BAD_REQUEST, null)
        }

        val headers = HttpHeaders()
        headers.cacheControl = CacheControl.noCache().headerValue
        headers.set("Content-Type", response.headers["Content-Type"])
        headers.set("Content-Disposition", response.headers["Content-Disposition"])
        return ResponseEntity(response.content, headers, HttpStatus.OK)
    }

    @PostMapping("user/{userId}/photo", consumes = ["multipart/form-data"])
    @Throws(IOException::class)
    @Secured(AuthorityRole.ROLE_USER)
    fun saveUserPhoto(
            @PathVariable userId: Long,
            @RequestParam("photo") photo: MultipartFile,
            request: HttpServletRequest): ResponseEntity<Void> {
        assertBinaryDataStoreEndpoint()

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))!!.id != userId) {
            throw RestException("Forbidden.", HttpStatus.FORBIDDEN, null)
        }

        multipartFormData(
                url = "${binaryDataStoreEndpoint}user/$userId/photo",
                fileInputStream = photo.inputStream,
                mimeType = photo.contentType!!,
                fileFieldName = "photo",
                fileName = "${userId}photo"
        )

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("game/{gameId}/replay", consumes = ["multipart/form-data"])
    @Throws(IOException::class)
    @Secured(AuthorityRole.ROLE_USER)
    fun reportGameWithReplayFile(
            @PathVariable gameId: Long,
            @RequestParam("replay") replay: MultipartFile,
            @RequestParam("score-home") scoreHome: Int,
            @RequestParam("score-away") scoreAway: Int,
            @RequestParam("home-user") homeUser: Long,
            @RequestParam("away-user") awayUser: Long,
            request: HttpServletRequest): ResponseEntity<GameCreationDto> {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser!!.id != homeUser && authUser.id != awayUser) {
            throw RestException("Please report your own games.", HttpStatus.FORBIDDEN, null)
        }

        val game: Game
        try {
            game = gameService.reportGame(homeUser, awayUser, scoreHome, scoreAway, replay)
            GlobalScope.launch {
                messageService.publishNews(
                        MessageNewsType.REPORT, authUser, game.id,
                        game.homeUser!!.username, game.awayUser!!.username,
                        game.scoreHome, game.scoreAway)
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

    fun multipartFormData(url: String, fileInputStream: InputStream, mimeType: String,
                          fileFieldName: String, fileName: String): HttpEntity {
        val uploadFile = HttpPost(url)
        uploadFile.entity = MultipartEntityBuilder.create()
                .addBinaryBody(
                        fileFieldName, fileInputStream,
                        ContentType.getByMimeType(mimeType), fileName)
                .build()
        return HttpClients.createDefault().execute(uploadFile).entity
    }

    @DeleteMapping("user/{userId}/photo")
    @Throws(IOException::class)
    @Secured(AuthorityRole.ROLE_USER)
    fun deleteUserPhoto(@PathVariable userId: Long,
                        request: HttpServletRequest): ResponseEntity<Void> {
        assertBinaryDataStoreEndpoint()

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))!!.id != userId) {
            throw RestException("Forbidden.", HttpStatus.FORBIDDEN, null)
        }

        val response = khttp.delete(
                url = "${binaryDataStoreEndpoint}user/$userId/photo")

        if (response.statusCode != 200) {
            logger.error("HTTP ${response.statusCode}: ${response.content.toString(Charset.defaultCharset())}")
            throw RestException("Ew, something went wrong.", HttpStatus.BAD_REQUEST, null)
        }

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @Throws(RestException::class)
    private fun assertBinaryDataStoreEndpoint() {
        binaryDataStoreEndpoint ?: throw RestException(
                "Replay upload is currently not supported.", HttpStatus.BAD_REQUEST, null)
    }
}
