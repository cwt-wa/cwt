package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Prod
import com.cwtsite.cwt.domain.core.WrappedCloseable
import khttp.responses.Response
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Prod
@Service
class BinaryOutboundServiceProdImpl : BinaryOutboundService {

    @Value("\${binary-data-store:#{null}}") private var binaryDataStoreEndpoint: String? = null
    @Value("\${waaas-endpoint:#{null}}") private var waaasEndpoint: String? = null
    @Value("\${cwt.third-party-token}") private lateinit var thirdPartyToken: String

    override fun retrieveUserPhoto(userId: Long): Response =
            khttp.get(url = "${binaryDataStoreEndpoint}/user/$userId/photo")

    override fun retrieveReplay(gameId: Long): Response =
            khttp.get(url = "${binaryDataStoreEndpoint}/game/$gameId/replay")

    override fun retrieveMap(gameId: Long, map: String): Response =
            khttp.get(url = "$binaryDataStoreEndpoint/game/$gameId/map/$map")

    override fun deleteUserPhoto(userId: Long): Response =
            khttp.delete(url = "${binaryDataStoreEndpoint}/user/$userId/photo")

    override fun sendUserPhoto(userId: Long, photo: File): CloseableHttpResponse =
            sendMultipartEntity(
                    url = "${binaryDataStoreEndpoint}/user/$userId/photo",
                    file = photo,
                    fileFieldName = "photo")

    override fun sendReplay(gameId: Long, replayArchive: File): CloseableHttpResponse {
        return sendMultipartEntity(
                url = "${binaryDataStoreEndpoint}/game/$gameId/replay",
                file = replayArchive,
                fileFieldName = "replay")
    }

    override fun extractGameStats(gameId: Long, extractedReplay: File) =
            sendMultipartEntity(
                    url = waaasEndpoint!!,
                    fileFieldName = "replay",
                    file = extractedReplay)

    override fun sendMap(response: String, gameId: Long, map: String): WrappedCloseable<File> {
        val createTempFile = createTempFile()
        createTempFile.writeBytes(khttp.get("${waaasEndpoint}$map").content)
        sendMultipartEntity(
                url = "${binaryDataStoreEndpoint}/game/$gameId/map/${map.split('/').last()}",
                file = createTempFile,
                fileFieldName = "map")
        return WrappedCloseable(createTempFile) { createTempFile.deleteRecursively() }
    }

    fun sendMultipartEntity(url: String, file: File, fileFieldName: String): CloseableHttpResponse {
        val multipartEntity = with(HttpPost(url)) {
            addHeader("third-party-token", thirdPartyToken)
            entity = MultipartEntityBuilder.create()
                    .addBinaryBody(
                            fileFieldName, file,
                            ContentType.DEFAULT_BINARY, file.nameWithoutExtension)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .build()
            this
        }

        return HttpClients.createDefault().execute(multipartEntity)
    }

    override fun binaryDataStoreConfigured(): Boolean = binaryDataStoreEndpoint != null

    override fun waaasConfigured(): Boolean = waaasEndpoint != null
}
