package com.cwtsite.cwt.core.news

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.entity.Comment
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class NewsAspect(private val messageService: MessageService,
                 private val securityContextHolderFacade: SecurityContextHolderFacade,
                 private val userRepository: UserRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)


    @AfterReturning(pointcut = "@annotation(PublishNews)", returning = "subject")
    fun publishNews(subject: Any?) {
        if (subject == null) {
            logger.warn("Not publishing news as \"subject\" is null")
            return
        }
        val authenticationName = securityContextHolderFacade.authenticationName
        if (authenticationName == null) {
            logger.warn("News cannot be published as there's no authenticated user.")
            return
        }
        val author = userRepository.findByUsername(authenticationName)!!
        logger.info("Publishing ${subject::class}")
        when (subject) {
            is Game -> {
                val messageNewsType = if (subject.voided) MessageNewsType.VOIDED else MessageNewsType.REPORT
                messageService.publishNews(
                        messageNewsType, author, subject.id!!,
                        subject.homeUser!!.username, subject.awayUser!!.username,
                        subject.scoreHome, subject.scoreAway
                )
            }
            is Rating -> {
                messageService.publishNews(
                        MessageNewsType.RATING, author, subject.game.id,
                        subject.game.homeUser!!.username, subject.game.awayUser!!.username,
                        subject.game.scoreHome, subject.game.scoreAway, subject.type.name.toLowerCase())
            }
            is Comment -> {
                messageService.publishNews(
                        MessageNewsType.COMMENT, author, subject.game!!.id!!,
                        subject.game!!.homeUser!!.username, subject.game!!.awayUser!!.username,
                        subject.game!!.scoreHome, subject.game!!.scoreAway)
            }
            else -> {
                logger.warn("Not publishing news as there's no handler for ${subject::class}")
            }
        }
    }
}
