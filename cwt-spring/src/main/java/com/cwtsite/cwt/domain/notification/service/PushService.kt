package com.cwtsite.cwt.domain.notification.service

import com.cwtsite.cwt.core.HttpClient
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.notification.NotificationType
import com.cwtsite.cwt.domain.notification.NotificationType.COMMENTED_GAME
import com.cwtsite.cwt.domain.notification.NotificationType.PRIVATE_MESSAGE
import com.cwtsite.cwt.domain.notification.NotificationType.PUBLIC_CHAT
import com.cwtsite.cwt.domain.notification.NotificationType.RATED_GAME
import com.cwtsite.cwt.domain.notification.NotificationType.RECORDED_LIVE_STREAM
import com.cwtsite.cwt.domain.notification.NotificationType.REPORTED_GAME
import com.cwtsite.cwt.domain.notification.NotificationType.SCHEDULED_GAME
import com.cwtsite.cwt.domain.notification.NotificationType.SCHEDULED_LIVE_STREAM
import com.cwtsite.cwt.domain.notification.NotificationType.VOIDED_GAME
import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
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
    @Value("\${cwt.push-server:#{null}}") private val pushServer: String? = null
) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun push(message: Message): PushNotification {
        var private: Boolean
        val subs = when (message.category) {
            MessageCategory.PRIVATE -> notificationRepository.findByUserIn(message.recipients)
                .filter { PRIVATE_MESSAGE.on(it.setting) }
                .map { it.subscription }
                .also { private = true }

            MessageCategory.SHOUTBOX -> subscribers(PUBLIC_CHAT, message.author).also { private = false }

            else -> throw RuntimeException("${message.category} cannot be pushed")
        }
        return PushNotification(
            title = (if (private) PRIVATE_MESSAGE else PUBLIC_CHAT).title,
            body = "from " + message.author.username,
            tag = (if (private) PRIVATE_MESSAGE else PUBLIC_CHAT).tag(message.author.id.toString()),
        ).also { push(it, subs) }
    }

    fun pushReport(g: Game): PushNotification =
        PushNotification(
            title = REPORTED_GAME.title,
            body = "${g.homeUser!!.username} ${g.scoreHome}–${g.scoreAway} ${g.awayUser!!.username}",
            tag = REPORTED_GAME.tag(g.id!!.toString()),
        ).also { push(it, subscribers(REPORTED_GAME, g.reporter)) }

    fun pushVoid(g: Game): PushNotification =
        PushNotification(
            title = VOIDED_GAME.title,
            body = "${g.homeUser!!.username} ${g.scoreHome}–${g.scoreAway} ${g.awayUser!!.username}",
            tag = VOIDED_GAME.tag(g.id!!.toString()),
        ).also { push(it, subscribers(VOIDED_GAME)) }

    fun push(rating: Rating): PushNotification =
        rating.game.let { g ->
            PushNotification(
                title = RATED_GAME.title,
                body = "${g.homeUser!!.username} ${g.scoreHome}–${g.scoreAway} ${g.awayUser!!.username}",
                tag = RATED_GAME.tag(g.id!!.toString()),
            ).also { push(it, subscribers(RATED_GAME, rating.user)) }
        }

    fun push(comment: Comment): PushNotification =
        comment.game!!.let { g ->
            PushNotification(
                title = COMMENTED_GAME.title,
                body = "${g.homeUser!!.username} ${g.scoreHome}–${g.scoreAway} ${g.awayUser!!.username}",
                tag = COMMENTED_GAME.tag(g.id!!.toString()),
            ).also { push(it, subscribers(COMMENTED_GAME, comment.author)) }
        }

    fun pushStreamSchedule(schedule: Schedule, cancelled: Boolean) =
        schedule.let { s ->
            PushNotification(
                title = if (cancelled) "Cancelled Live Stream" else SCHEDULED_LIVE_STREAM.title,
                body = "${s.homeUser.username} vs. ${s.awayUser.username}",
                tag = SCHEDULED_LIVE_STREAM.tag(s.id.toString()),
            ).also { push(it, subscribers(SCHEDULED_LIVE_STREAM)) }
        }

    fun pushGameSchedule(schedule: Schedule, cancelled: Boolean) =
        schedule.let { s ->
            PushNotification(
                title = if (cancelled) "Cancelled Game" else SCHEDULED_GAME.title,
                body = "${s.homeUser.username} vs. ${s.awayUser.username}",
                tag = SCHEDULED_GAME.tag(s.id.toString()),
            ).also { push(it, subscribers(SCHEDULED_GAME, schedule.author)) }
        }

    fun push(stream: Stream)  =
        stream.game!!.let { g ->
            PushNotification(
                title = RECORDED_LIVE_STREAM.title,
                body = "${g.homeUser!!.username} ${g.scoreHome}–${g.scoreAway} ${g.awayUser!!.username}",
                tag = RECORDED_LIVE_STREAM.tag(g.id!!.toString()),
            ).also { push(it, subscribers(RECORDED_LIVE_STREAM)) }
        }

    private fun subscribers(type: NotificationType, excl: User? = null): List<String> =
        notificationRepository.findAll()
            .filter { type.on(it.setting) }
            .filter { excl == null || excl != it.user }
            .map { it.subscription }

    private fun push(n: PushNotification, subscriptions: List<String>) {
        if (pushServer == null) {
            logger.info("No Push Server configured")
            return
        }
        logger.info("Pushing ${n.tag} to ${subscriptions.size} subscribers")
        val res = httpClient.request(
            HttpRequest.newBuilder()
                .headers("third-party-token", thirdPartyToken)
                .uri(URI.create("$pushServer/pub"))
                .POST(HttpRequest.BodyPublishers.ofString(JSONObject(n.toRequest(subscriptions)).toString()))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
        logger.info("Push Server response: " + res.statusCode())
    }
}
