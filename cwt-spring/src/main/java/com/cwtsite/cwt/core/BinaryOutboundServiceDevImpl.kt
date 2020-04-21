package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Dev
import com.cwtsite.cwt.domain.core.WrappedCloseable
import khttp.responses.Response
import org.apache.http.client.methods.CloseableHttpResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Dev
@Service
class BinaryOutboundServiceDevImpl : BinaryOutboundService {

    @Value("\${binary-data-store:#{null}}")
    private var binaryDataStoreEndpoint: String? = null

    @Value("\${waaas-endpoint:#{null}}")
    private var waaasEndpoint: String? = null

    override fun retrieveUserPhoto(userId: Long): Response {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun retrieveReplay(gameId: Long): Response {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun retrieveMap(gameId: Long, map: String): Response {
        throw UnsupportedOperationException("Not implemented")
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
