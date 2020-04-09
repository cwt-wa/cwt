package com.cwtsite.cwt.core

import khttp.responses.Response
import org.apache.http.HttpEntity
import org.springframework.web.multipart.MultipartFile
import java.io.File


interface BinaryOutboundService {

    fun retrieveUserPhoto(userId: Long): Response
    fun retrieveReplay(gameId: Long): Response
    fun deleteUserPhoto(userId: Long): Response
    fun sendUserPhoto(userId: Long, photo: MultipartFile): HttpEntity
    fun sendReplay(gameId: Long, replay: MultipartFile): HttpEntity
    fun extractGameStats(gameId: Long, extractedReplay: File): HttpEntity
    fun assertBinaryDataStoreEndpoint()
}
