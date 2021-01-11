package com.cwtsite.cwt.core

import java.net.http.HttpResponse
import java.io.File
import java.io.InputStream


interface BinaryOutboundService {

    fun retrieveUserPhoto(userId: Long): HttpResponse<InputStream>
    fun retrieveReplay(gameId: Long): HttpResponse<InputStream>
    fun retrieveMap(gameId: Long, map: String): HttpResponse<InputStream>
    fun deleteUserPhoto(userId: Long): HttpResponse<String>
    fun sendUserPhoto(userId: Long, photo: File): HttpResponse<String>
    fun sendReplay(gameId: Long, replayArchive: File): HttpResponse<String>
    fun extractGameStats(gameId: Long, extractedReplay: File): HttpResponse<String>
    fun sendMap(gameId: Long, map: String): HttpResponse<String>
    fun binaryDataStoreConfigured(): Boolean
    fun waaasConfigured(): Boolean
}

