package com.cwtsite.cwt.core

import com.cwtsite.cwt.core.profile.Prod
import khttp.responses.Response
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import org.springframework.stereotype.Service
import java.io.InputStream

@Prod
@Service
class BinaryOutboundServiceProdImpl : BinaryOutboundService {

    override fun get(url: String): Response = khttp.get(url = url)

    override fun post(url: String): Response = khttp.post(url = url)

    override fun delete(url: String): Response = khttp.delete(url = url)

    override fun sendMultipartEntity(url: String, fileInputStream: InputStream, mimeType: String,
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
