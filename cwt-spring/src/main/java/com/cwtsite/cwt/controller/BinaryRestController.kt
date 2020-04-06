package com.cwtsite.cwt.controller

import com.cwtsite.cwt.core.FileValidator
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.AuthService
import khttp.responses.Response
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
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

    @Value("\${binary-data-store}")
    private var binaryDataStoreEndpoint: String? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var authService: AuthService

    @GetMapping("user/{userId}/photo")
    fun getUserPhoto(@PathVariable userId: Long): ResponseEntity<ByteArray> {
        assertBinaryDataStoreEndpoint()
        val response = khttp.get(url = "${binaryDataStoreEndpoint}/user/$userId/photo")
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

        sendMultipartEntity(
                url = "${binaryDataStoreEndpoint}/user/$userId/photo",
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
    fun saveReplayFile(
            @PathVariable gameId: Long,
            @RequestParam("replay") replay: MultipartFile,
            @RequestParam("score-home") scoreHome: Int,
            @RequestParam("score-away") scoreAway: Int,
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
                    listOf("application/x-rar", "application/x-rar-compressed",
                            "application/zip", "application/x-zip-compressed",
                            "application/octet-stream"),
                    listOf("rar", "zip"))
        } catch (e: FileValidator.AbstractFileException) {
            throw RestException(e.message!!, HttpStatus.BAD_REQUEST, e)
        }

        sendMultipartEntity(
                url = "${binaryDataStoreEndpoint}/game/$gameId/replay",
                fileInputStream = replay.inputStream,
                mimeType = replay.contentType!!,
                fileFieldName = "replay",
                fileName = "${gameId}replay"
        )

        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("game/{gameId}/replay")
    fun getReplayFile(@PathVariable gameId: Long): ResponseEntity<ByteArray> {
        assertBinaryDataStoreEndpoint()
        val response = khttp.get(url = "${binaryDataStoreEndpoint}/game/$gameId/replay")
        if (assertResponse(response)) return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        return createResponseEntity(response.headers, response.content)
    }

    fun sendMultipartEntity(url: String, fileInputStream: InputStream, mimeType: String,
                            fileFieldName: String, fileName: String) {
        val multipartEntity = with(HttpPost(url)) {
            entity = MultipartEntityBuilder.create()
                    .addBinaryBody(
                            fileFieldName, fileInputStream,
                            ContentType.DEFAULT_BINARY, fileName)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .build()
            this
        }

        HttpClients.createDefault().use { it.execute(multipartEntity) }
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
                url = "${binaryDataStoreEndpoint}/user/$userId/photo")

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
    private fun assertBinaryDataStoreEndpoint() {
        binaryDataStoreEndpoint ?: throw RestException(
                "Replay upload is currently not supported.", HttpStatus.BAD_REQUEST, null)
    }
}
