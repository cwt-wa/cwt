package com.cwtsite.cwt.domain.notification.service

import com.cwtsite.cwt.core.HttpClient
import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.notification.NotificationType.PRIVATE_MESSAGE
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class PushService @Autowired
constructor(
    private val httpClient: HttpClient,
    private val notificationRepository: NotificationRepository,
    @Value("\${cwt.third-party-token}") private val thirdPartyToken: String,
    @Value("\${pushServer:#{null}}") private val pushServer: String? = null
) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun push(message: Message): PushNotification {
        val subscriptions = notificationRepository.findByUserIn(message.recipients)
            .filter { PRIVATE_MESSAGE.on(it.setting) }
            .map { it.subscription }
        return PushNotification(
            title = PRIVATE_MESSAGE.title,
            body = "from " + message.author.username,
            tag = PRIVATE_MESSAGE.tag(message.author.id.toString()),
        ).also { push(it, subscriptions) }
    }

    private fun push(n: PushNotification, subscriptions: List<String>) {
        if (pushServer == null) {
            logger.info("No Push Server configured")
            return
        }
        val res = httpClient.request(
            HttpRequest.newBuilder()
                .headers("third-party-token", thirdPartyToken)
                .uri(URI.create(pushServer))
                .POST(HttpRequest.BodyPublishers.ofString(JSONObject(n.toRequest(subscriptions)).toString()))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
        logger.info("Push Server response: " + res.statusCode())
    }
}
