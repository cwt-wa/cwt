package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Dev
import com.cwtsite.cwt.domain.core.WrappedCloseable
import khttp.requests.Request
import khttp.responses.Response
import khttp.structures.cookie.CookieJar
import org.apache.http.client.methods.CloseableHttpResponse
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.nio.charset.Charset

@Dev
@Service
class BinaryOutboundServiceDevImpl() : BinaryOutboundService {

    @Value("\${binary-data-store:#{null}}")
    private var binaryDataStoreEndpoint: String? = null

    @Value("\${waaas-endpoint:#{null}}")
    private var waaasEndpoint: String? = null

    private val map: ByteArray = this.javaClass.getResource("/map.png").readBytes()

    override fun retrieveUserPhoto(userId: Long): Response {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun retrieveReplay(gameId: Long): Response {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun retrieveMap(gameId: Long, map: String): Response {
        return object : Response {
            override val connection: HttpURLConnection
                get() = throw UnsupportedOperationException("not available in this mock")
            override val content: ByteArray
                get() = this@BinaryOutboundServiceDevImpl.map
            override val cookies: CookieJar
                get() = throw UnsupportedOperationException("not available in this mock")
            override var encoding: Charset
                get() = throw UnsupportedOperationException("not available in this mock")
                @Suppress("UNUSED_PARAMETER")
                set(value) {}
            override val headers: Map<String, String>
                get() = mapOf(
                        "cache-control" to "immutable, max-age=604800, public",
                        "content-type" to "image/png",
                        "content-disposition" to "attachment; filename=1i2myw9o.png"
                )
            override val history: List<Response>
                get() = throw UnsupportedOperationException("not available in this mock")
            override val jsonArray: JSONArray
                get() = throw UnsupportedOperationException("not available in this mock")
            override val jsonObject: JSONObject
                get() = throw UnsupportedOperationException("not available in this mock")
            override val raw: InputStream
                get() = throw UnsupportedOperationException("not available in this mock")
            override val request: Request
                get() = throw UnsupportedOperationException("not available in this mock")
            override val statusCode: Int
                get() = throw UnsupportedOperationException("not available in this mock")
            override val text: String
                get() = throw UnsupportedOperationException("not available in this mock")
            override val url: String
                get() = throw UnsupportedOperationException("not available in this mock")

            override fun contentIterator(chunkSize: Int): Iterator<ByteArray> {
                throw UnsupportedOperationException("not available in this mock")
            }

            override fun lineIterator(chunkSize: Int, delimiter: ByteArray?): Iterator<ByteArray> {
                throw UnsupportedOperationException("not available in this mock")
            }

        }
    }

    override fun deleteUserPhoto(userId: Long): Response {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendUserPhoto(userId: Long, photo: File): CloseableHttpResponse {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendReplay(gameId: Long, replayArchive: File): CloseableHttpResponse {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun extractGameStats(gameId: Long, extractedReplay: File): CloseableHttpResponse {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendMap(response: String, gameId: Long, map: String): WrappedCloseable<File> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun binaryDataStoreConfigured(): Boolean = binaryDataStoreEndpoint != null

    override fun waaasConfigured(): Boolean = waaasEndpoint != null
}
