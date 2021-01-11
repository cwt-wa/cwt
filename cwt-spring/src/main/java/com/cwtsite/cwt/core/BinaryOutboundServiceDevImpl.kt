package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Dev
import com.cwtsite.cwt.domain.core.WrappedCloseable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.net.http.HttpResponse

@Dev
@Service
class BinaryOutboundServiceDevImpl() : BinaryOutboundService {

    @Value("\${binary-data-store:#{null}}")
    private var binaryDataStoreEndpoint: String? = null

    @Value("\${waaas-endpoint:#{null}}")
    private var waaasEndpoint: String? = null

    private val map: ByteArray = this.javaClass.getResource("/map.png").readBytes()

    override fun retrieveUserPhoto(userId: Long): HttpResponse<InputStream> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun retrieveReplay(gameId: Long): HttpResponse<InputStream> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun retrieveMap(gameId: Long, map: String): HttpResponse<InputStream> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun deleteUserPhoto(userId: Long): HttpResponse<String> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendUserPhoto(userId: Long, photo: File): HttpResponse<String> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendReplay(gameId: Long, replayArchive: File): HttpResponse<String> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun extractGameStats(gameId: Long, extractedReplay: File): HttpResponse<String> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun sendMap(response: String, gameId: Long, map: String): WrappedCloseable<File> {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun binaryDataStoreConfigured(): Boolean = binaryDataStoreEndpoint != null

    override fun waaasConfigured(): Boolean = waaasEndpoint != null
}

