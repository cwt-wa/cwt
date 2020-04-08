package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Dev
import khttp.responses.Response
import org.apache.http.HttpEntity
import org.springframework.stereotype.Service
import java.io.InputStream

@Dev
@Service
class BinaryOutboundServiceDevImpl : BinaryOutboundService {

    override fun get(url: String): Response {
        TODO("Not yet implemented")
    }

    override fun post(url: String): Response {
        TODO("Not yet implemented")
    }

    override fun delete(url: String): Response {
        TODO("Not yet implemented")
    }

    override fun sendMultipartEntity(url: String, fileInputStream: InputStream, mimeType: String, fileFieldName: String, fileName: String): HttpEntity {
        TODO("Not yet implemented")
    }
}
