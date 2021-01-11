package com.cwtsite.cwt.core

import com.cwtsite.cwt.domain.core.WrappedCloseable
import java.net.http.HttpResponse
import org.apache.http.client.methods.CloseableHttpResponse
import java.io.File
import java.io.InputStream


interface BinaryOutboundService {

    fun retrieveUserPhoto(userId: Long): HttpResponse<InputStream>
    fun retrieveReplay(gameId: Long): HttpResponse<InputStream>
    fun retrieveMap(gameId: Long, map: String): HttpResponse<InputStream>
    fun deleteUserPhoto(userId: Long): HttpResponse<String>
    fun sendUserPhoto(userId: Long, photo: File): CloseableHttpResponse
    fun sendReplay(gameId: Long, replayArchive: File): CloseableHttpResponse
    fun extractGameStats(gameId: Long, extractedReplay: File): CloseableHttpResponse
    fun sendMap(response: String, gameId: Long, map: String): WrappedCloseable<File>
    fun binaryDataStoreConfigured(): Boolean
    fun waaasConfigured(): Boolean
}

