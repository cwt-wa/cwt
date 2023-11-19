package com.cwtsite.cwt.core.news

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.notification.service.PushService
import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.entity.Comment
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Aspect
@Component
class NewsAspect(
    private val messageService: MessageService,
    private val securityContextHolderFacade: SecurityContextHolderFacade,
    private val userRepository: UserRepository,
    private val pushService: PushService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @AfterReturning(pointcut = "@annotation(PublishNews)", returning = "subject")
    fun publishNews(jp: JoinPoint, subject: Any?) {
        logger.info("PointCut JoinPoint $jp")
        if (subject == null) {
            logger.warn("Not publishing news as \"subject\" is null")
            return
        }
        val author = when (subject) {
            is Stream -> {
                logger.info("Subject is Stream, author is the channel's owner ${subject.channel.user}")
                subject.channel.user
            }
            else -> {
                val name = securityContextHolderFacade.authenticationName ?: run {
                    logger.warn("News cannot be published as there's no authenticated user.")
                    return@publishNews
                }
                userRepository.findByUsername(name)!!
            }
        }
        logger.info("Publishing ${subject::class}")
        when (subject) {
            is Game -> {
                val messageNewsType = if (subject.voided) MessageNewsType.VOIDED else MessageNewsType.REPORT
                messageService.publishNews(
                    messageNewsType, author, subject.id!!,
                    subject.homeUser!!.username, subject.awayUser!!.username,
                    subject.scoreHome, subject.scoreAway
                )
                if (subject.voided) pushService.pushVoid(subject) else pushService.pushReport(subject)
            }
            is Rating -> {
                messageService.publishNews(
                    MessageNewsType.RATING, author, subject.game.id,
                    subject.game.homeUser!!.username, subject.game.awayUser!!.username,
                    subject.game.scoreHome, subject.game.scoreAway, subject.type.name.toLowerCase()
                )
                pushService.push(subject)
            }
            is Comment -> {
                messageService.publishNews(
                    MessageNewsType.COMMENT, author, subject.game!!.id!!,
                    subject.game!!.homeUser!!.username, subject.game!!.awayUser!!.username,
                    subject.game!!.scoreHome, subject.game!!.scoreAway
                )
                pushService.push(subject)
            }
            is Stream -> {
                messageService.publishNews(
                    MessageNewsType.STREAM, subject.channel.user,
                    subject.id, subject.title
                )
                pushService.push(subject)
            }
            is Schedule -> {
                val scheduleStream = "scheduleStream"
                val technicallyDeleted = "deleteSchedule"
                val removeStream = "removeStream"
                val createSchedule = "createSchedule"
                val cancelSchedule = "cancelSchedule"
                val methods = arrayOf(
                    removeStream, scheduleStream,
                    createSchedule, technicallyDeleted, cancelSchedule
                )
                val method = runCatching { jp.signature.name }.getOrElse {
                    logger.error("Error when getting method name", it)
                    logger.info("Falling back to $scheduleStream")
                    scheduleStream
                }.let {
                    if (!methods.contains(it)) {
                        logger.warn("$it was called, but is not handled by $methods")
                        logger.info("Falling back to $scheduleStream")
                        scheduleStream
                    } else {
                        it
                    }
                }
                if (method == technicallyDeleted) {
                    logger.info("No news when schedule was technically deleted")
                } else {
                    val appointment: String =
                        subject.appointment.atZone(ZoneOffset.UTC).toString()
                    messageService.publishNews(
                        MessageNewsType.SCHEDULE, author, method,
                        subject.homeUser.username, subject.awayUser.username,
                        appointment
                    )
                    when (method) {
                        scheduleStream, removeStream -> pushService.pushStreamSchedule(subject, method == removeStream)
                        createSchedule, cancelSchedule -> pushService.pushGameSchedule(subject, method == cancelSchedule)
                    }
                }
            }
            else -> {
                logger.warn("Not publishing news as there's no handler for ${subject::class}")
            }
        }
    }
}
