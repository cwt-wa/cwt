package com.cwtsite.cwt.domain.core.view.controller

import com.cwtsite.cwt.controller.RestException
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.Charset

@RestController
@RequestMapping("api/binary")
class BinaryRestController {

    private val binaryDatStoreEndpoint = "http://cwt-binary.normalnonoobs.com/api/"

    @GetMapping("user/{userId}/photo")
    fun getUserPhoto(@PathVariable userId: Long): ResponseEntity<ByteArray> {
        val response = khttp.get(
                url = "${binaryDatStoreEndpoint}user/$userId/photo")

        if (response.statusCode != 200) {
            val err = """Captcha token validation status code ${response.statusCode}
                |${response.content.toString(Charset.defaultCharset())}
            """
            throw RestException(err, HttpStatus.BAD_REQUEST, null)
        }

        val headers = HttpHeaders()
        headers.cacheControl = CacheControl.noCache().headerValue
        headers.set("Content-Type", response.headers["Content-Type"])
        headers.set("Content-Disposition", response.headers["Content-Disposition"])
        return ResponseEntity(response.content, headers, HttpStatus.OK)
    }
}
