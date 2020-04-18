package com.cwtsite.cwt.controller

import com.cwtsite.cwt.core.BinaryOutboundService
import com.cwtsite.cwt.core.FileValidator
import com.cwtsite.cwt.core.MultipartFileToFile.convertMultipartFileToFile
import com.cwtsite.cwt.domain.core.Unzip
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.AuthService
import khttp.responses.Response
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Produces

@RestController
@RequestMapping("api/binary")
class BinaryRestController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var binaryOutboundService: BinaryOutboundService

    @GetMapping("user/{userId}/photo")
    fun getUserPhoto(@PathVariable userId: Long): ResponseEntity<ByteArray> {
        assertBinaryDataStoreEndpoint()
        val response = binaryOutboundService.retrieveUserPhoto(userId)
        if (assertResponse(response)) return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        return createResponseEntity(response.headers, response.content)
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

        try {
            FileValidator.validate(
                    photo, 3000000,
                    listOf("image/jpeg", "image/png", "image/gif"),
                    listOf("jpg", "jpeg", "png", "gif"))
        } catch (e: FileValidator.AbstractFileException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        }

        binaryOutboundService.sendUserPhoto(userId, convertMultipartFileToFile(photo))

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("game/{gameId}/replay", consumes = ["multipart/form-data"])
    @Throws(IOException::class)
    @Secured(AuthorityRole.ROLE_USER)
    fun saveReplayFile(
            @PathVariable gameId: Long,
            @RequestParam("replay") replay: MultipartFile,
            @RequestParam("home-user") homeUser: Long,
            @RequestParam("away-user") awayUser: Long,
            request: HttpServletRequest): ResponseEntity<GameCreationDto> {
        assertBinaryDataStoreEndpoint()

        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        if (authUser!!.id != homeUser && authUser.id != awayUser) {
            throw RestException("Please report your own games.", HttpStatus.FORBIDDEN, null)
        }

        try {
            FileValidator.validate(
                    replay, 150000,
                    listOf("application/zip", "application/x-zip-compressed",
                            "application/octet-stream"),
                    listOf("zip"))
        } catch (e: FileValidator.AbstractFileException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        }

        binaryOutboundService.sendReplay(gameId, convertMultipartFileToFile(replay))

        GlobalScope.launch {
            try {
                val game = gameService.findById(gameId)
                if (game.isEmpty) {
                    logger.warn("Game with ID $gameId could not be retrieved for replay WAaaS extraction.")
                } else {
                    extractAndSaveGameStats(replay, game.get())
                }
            } catch (e: Exception) {
                logger.error("WAaaS replay extraction went wrong", e)
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("game/{gameId}/stats", consumes = ["multipart/form-data"])
    @Secured(AuthorityRole.ROLE_ADMIN)
    @Produces("application/json")
    fun saveStats(
            @PathVariable gameId: Long,
            @RequestParam("replay") replay: MultipartFile,
            request: HttpServletRequest): ResponseEntity<String> {
        val game = gameService.findById(gameId)
                .orElseThrow { RestException("No such game", HttpStatus.NOT_FOUND, null) }
        extractAndSaveGameStats(replay, game)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.findGameStats(game))
    }

    private fun extractAndSaveGameStats(replay: MultipartFile, game: Game) {
        if (!binaryOutboundService.waaasConfigured()) {
            logger.info("Not performing game stats extraction as WAaaS is not configured.")
            return
        }

        replay.inputStream.use { zipArchiveInputStream ->
            Unzip.unzipReplayFiles(zipArchiveInputStream).use { extracted ->
                runBlocking {
                    extracted.content.forEach { extractedReplay ->
                        launch {
                            try {
                                binaryOutboundService.extractGameStats(game.id!!, extractedReplay).use { response ->
                                    gameService.saveGameStats(
                                            response.entity.content
                                                    .bufferedReader()
                                                    .use(BufferedReader::readText),
                                            game)
                                }

                            } catch (e: Exception) {
                                logger.error("Replay stats could not be extracted.", e)
                            }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("game/{gameId}/replay")
    fun getReplayFile(@PathVariable gameId: Long): ResponseEntity<ByteArray> {
        assertBinaryDataStoreEndpoint()
        val response = binaryOutboundService.retrieveReplay(gameId)
        if (assertResponse(response)) return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        return createResponseEntity(response.headers, response.content)
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

        val response = binaryOutboundService.deleteUserPhoto(userId)

        if (response.statusCode != 200) {
            logger.error("HTTP ${response.statusCode}: ${response.content.toString(Charset.defaultCharset())}")
            throw RestException("Ew, something went wrong.", HttpStatus.BAD_REQUEST, null)
        }

        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @Throws(RestException::class)
    private fun assertResponse(response: Response): Boolean {
        if (response.statusCode != 200) {
            if (response.statusCode == 404) {
                return true
            }
            logger.error("HTTP ${response.statusCode}: ${response.content.toString(Charset.defaultCharset())}")
            throw RestException("Ew, something went wrong.", HttpStatus.BAD_REQUEST, null)
        }
        return false
    }

    private fun createResponseEntity(requestHeaders: Map<String, String>,
                                     fileContent: ByteArray): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.cacheControl = CacheControl.noCache().headerValue
        headers.set("Content-Type", requestHeaders["Content-Type"])
        headers.set("Content-Disposition", requestHeaders["Content-Disposition"])
        return ResponseEntity(fileContent, headers, HttpStatus.OK)
    }

    @Throws(RestException::class)
    fun assertBinaryDataStoreEndpoint() {
        if (!binaryOutboundService.binaryDataStoreConfigured()) {
            throw RestException(
                    "Replay upload is currently not supported.",
                    HttpStatus.BAD_REQUEST, null)
        }
    }
}
