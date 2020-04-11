package com.cwtsite.cwt.core

import khttp.responses.Response
import org.apache.http.client.methods.CloseableHttpResponse
import java.io.File


interface BinaryOutboundService {

    fun retrieveUserPhoto(userId: Long): Response
    fun retrieveReplay(gameId: Long): Response
    fun deleteUserPhoto(userId: Long): Response
    fun sendUserPhoto(userId: Long, photo: File): CloseableHttpResponse
    fun sendReplay(gameId: Long, replayArchive: File): CloseableHttpResponse
    fun extractGameStats(gameId: Long, extractedReplay: File): CloseableHttpResponse
    fun binaryDataStoreConfigured(): Boolean
    fun waaasConfigured(): Boolean
}
