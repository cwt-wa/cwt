package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.HttpClient
import com.cwtsite.cwt.core.profile.Prod
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.HttpRequest
import java.net.URI
import java.math.BigInteger
import java.util.Random
import java.io.File.createTempFile

@Prod
@Service
class BinaryOutboundServiceProdImpl : BinaryOutboundService {

    @Value("\${binary-data-store:#{null}}") private var binaryDataStoreEndpoint: String? = null
    @Value("\${waaas-endpoint:#{null}}") private var waaasEndpoint: String? = null
    @Value("\${cwt.third-party-token}") private lateinit var thirdPartyToken: String

    @Autowired private lateinit var httpClient: HttpClient

    override fun retrieveUserPhoto(userId: Long): HttpResponse<InputStream> =
            httpGetInputStream("${binaryDataStoreEndpoint}/user/$userId/photo")

    override fun retrieveReplay(gameId: Long): HttpResponse<InputStream> =
            httpGetInputStream("${binaryDataStoreEndpoint}/game/$gameId/replay")

    override fun retrieveMap(gameId: Long, map: String): HttpResponse<InputStream> =
            httpGetInputStream("$binaryDataStoreEndpoint/game/$gameId/map/$map")

    private fun httpGetInputStream(uri: String): HttpResponse<InputStream> =
            httpClient.request(
                HttpRequest.newBuilder().GET().uri(URI.create(uri)).build(),
                BodyHandlers.ofInputStream())

    override fun deleteUserPhoto(userId: Long): HttpResponse<String> =
            httpClient.request(
                HttpRequest.newBuilder()
                .DELETE().uri(URI.create("${binaryDataStoreEndpoint}/user/$userId/photo")).build(),
                BodyHandlers.ofString())

    override fun sendUserPhoto(userId: Long, photo: File): HttpResponse<String> =
            sendMultipartEntity(
                    uri = "${binaryDataStoreEndpoint}/user/$userId/photo",
                    file = photo,
                    fileFieldName = "photo")

    override fun sendReplay(gameId: Long, replayArchive: File): HttpResponse<String> {
        return sendMultipartEntity(
                uri = "${binaryDataStoreEndpoint}/game/$gameId/replay",
                file = replayArchive,
                fileFieldName = "replay")
    }

    override fun extractGameStats(gameId: Long, extractedReplay: File) =
            sendMultipartEntity(
                    uri = waaasEndpoint!!,
                    fileFieldName = "replay",
                    file = extractedReplay)

    override fun sendMap(gameId: Long, map: String): HttpResponse<String> {
        val response = httpClient.request(
                HttpRequest.newBuilder().GET().uri(URI.create("${waaasEndpoint}$map")).build(),
                BodyHandlers.ofByteArray())
        val tempFile = createTempFile("cwt", "map")
        try {
            tempFile.writeBytes(response.body())
            return sendMultipartEntity(
                    uri = "${binaryDataStoreEndpoint}/game/$gameId/map/${map.split('/').last()}",
                    file = tempFile,
                    fileFieldName = "map")
        } finally {
            tempFile.deleteRecursively()
        }
    }

    fun sendMultipartEntity(uri: String, file: File, fileFieldName: String): HttpResponse<String> =
            httpClient.request(
                HttpRequest.newBuilder()
                    .headers("third-party-token", thirdPartyToken)
                    .postMultipartFormData(
                            BigInteger(35, Random()).toString(),
                            mapOf(fileFieldName to file))
                    .uri(URI.create(uri)).build(),
                BodyHandlers.ofString())

    override fun binaryDataStoreConfigured(): Boolean =
            binaryDataStoreEndpoint != null && binaryDataStoreEndpoint?.isNotBlank() ?: false

    override fun waaasConfigured(): Boolean =
            waaasEndpoint != null && waaasEndpoint?.isNotBlank() ?: false
}

