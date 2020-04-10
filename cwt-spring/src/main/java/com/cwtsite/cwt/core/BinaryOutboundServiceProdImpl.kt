package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Prod
import khttp.responses.Response
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream

const val waGameMimeType = "application/wagame"

@Prod
@Service
class BinaryOutboundServiceProdImpl : BinaryOutboundService {

    @Value("\${binary-data-store:#{null}}")
    private var binaryDataStoreEndpoint: String? = null

    @Value("\${waaas-endpoint:#{null}}")
    private var waaasEndpoint: String? = null

    override fun retrieveUserPhoto(userId: Long): Response =
            get("${binaryDataStoreEndpoint}/user/$userId/photo")

    override fun retrieveReplay(gameId: Long): Response =
            get("${binaryDataStoreEndpoint}/game/$gameId/replay")

    override fun deleteUserPhoto(userId: Long): Response =
            delete(url = "${binaryDataStoreEndpoint}/user/$userId/photo")

    override fun sendUserPhoto(userId: Long, photo: MultipartFile): HttpEntity =
            sendMultipartEntity(
                    url = "${binaryDataStoreEndpoint}/user/$userId/photo",
                    fileInputStream = photo.inputStream,
                    mimeType = photo.contentType!!,
                    fileFieldName = "photo",
                    fileName = "${userId}photo")


    override fun sendReplay(gameId: Long, replayArchive: MultipartFile): HttpEntity =
            sendMultipartEntity(
                    url = "${binaryDataStoreEndpoint}/game/$gameId/replay",
                    fileInputStream = replayArchive.inputStream,
                    mimeType = replayArchive.contentType!!,
                    fileFieldName = "replay",
                    fileName = "${gameId}replay")

    override fun extractGameStats(gameId: Long, extractedReplay: File) =
            sendMultipartEntity(
                    url = waaasEndpoint!!,
                    fileInputStream = extractedReplay.inputStream(),
                    mimeType = waGameMimeType,
                    fileFieldName = "replay",
                    fileName = "${gameId}replay")

    override fun binaryDataStoreConfigured(): Boolean = binaryDataStoreEndpoint != null

    override fun waaasConfigured(): Boolean = waaasEndpoint != null

    fun get(url: String): Response = khttp.get(url = url)

    fun post(url: String): Response = khttp.post(url = url)

    fun delete(url: String): Response = khttp.delete(url = url)

    fun sendMultipartEntity(url: String, fileInputStream: InputStream, mimeType: String,
                            fileFieldName: String, fileName: String): HttpEntity {
        val multipartEntity = with(HttpPost(url)) {
            entity = MultipartEntityBuilder.create()
                    .addBinaryBody(
                            fileFieldName, fileInputStream,
                            ContentType.DEFAULT_BINARY, fileName)
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .build()
            this
        }

        return HttpClients.createDefault()
                .use { client -> client.execute(multipartEntity).use { response -> response.entity } }
    }
}
