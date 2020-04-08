package com.cwtsite.cwt.core

import khttp.responses.Response
import org.apache.http.HttpEntity
import java.io.InputStream


interface BinaryOutboundService {

    fun get(url: String): Response
    fun post(url: String): Response
    fun delete(url: String): Response
    fun sendMultipartEntity(url: String, fileInputStream: InputStream, mimeType: String,
                            fileFieldName: String, fileName: String): HttpEntity
}
