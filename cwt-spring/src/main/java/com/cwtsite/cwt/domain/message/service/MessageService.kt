package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.application.service.ApplicationRepository
import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Duration
import java.time.Instant
import kotlin.math.min

@Component
class MessageService @Autowired
constructor(
    private val messageRepository: MessageRepository,
    private val userService: UserService,
    private val messageEventListener: MessageEventListener,
    private val tournamentService: TournamentService,
    private val applicationRepository: ApplicationRepository
) {

    fun findNewMessages(
        after: Instant,
        size: Int,
        user: User?,
        categories: List<MessageCategory> = MessageCategory.values().toList()
    ): List<Message> {
        // offset by one because DB might store with greater precision
        val result = messageRepository.findNewByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(user, categories, after.plusMillis(1))
        return result.subList(0, min(size, result.size))
    }

    fun findOldMessages(
        before: Instant,
        size: Int,
        user: User?,
        categories: List<MessageCategory> = MessageCategory.values().toList()
    ): List<Message> {
        val result = messageRepository.findOldByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(user, categories, before)
        return result.subList(0, min(size, result.size))
    }

    @Transactional
    fun save(message: Message): Message {
        val persisted = messageRepository.save(message)
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                messageEventListener.publish(persisted)
            }
        })
        return persisted
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun publishNews(type: MessageNewsType, author: User, vararg data: Any?): Message {
        val body = when (type) {
            MessageNewsType.STREAM -> "${data[0]},${data.drop(1).joinToString("")}"
            MessageNewsType.TWITCH_MESSAGE, MessageNewsType.DISCORD_MESSAGE ->
                "${data[0]},${data[1]},${data.drop(2).joinToString("")}"
            else -> data.joinToString(separator = ",")
        }
        val persisted = messageRepository.save(
            Message(
                category = MessageCategory.NEWS,
                author = author, newsType = type, body = body
            )
        )
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                messageEventListener.publish(persisted)
            }
        })
        return persisted
    }

    fun deleteMessage(id: Long) {
        messageRepository.deleteById(id)
    }

    fun findMessagesForAdminCreatedBefore(before: Instant, size: Int): List<Message> {
        val result = messageRepository.findAllByCreatedBeforeOrderByCreatedDesc(before)
        return result.subList(0, min(size, result.size))
    }

    fun findMessagesForAdminCreatedAfter(after: Instant, size: Int): List<Message> {
        // offset by one because DB might store with greater precision
        val result = messageRepository.findAllByCreatedAfterOrderByCreatedDesc(after.plusMillis(1))
        return result.subList(0, min(size, result.size))
    }

    @Transactional
    fun thirdPartyMessage(
        displayName: String,
        link: String,
        body: String,
        newsType: MessageNewsType
    ): Message =
        publishNews(
            newsType,
            userService.getById(1).orElseThrow(),
            displayName, link, body
        )

    /**
     * The eagerly loaded suggestions are the remaining opponents,
     * the most recent private message interactions, the recent chatbox users,
     * the applicants to the current tournament.
     */
    fun genSuggestions(user: User): List<User> {
        val remainingOpponents = userService.getRemainingOpponents(user)
        val resList = remainingOpponents.toMutableList()
        val pms = messageRepository.findPrivateMessages(user)
        if (pms.isNotEmpty()) {
            var idx = 0
            val now = Instant.now()
            while (idx < pms.size && Duration.between(pms[idx].created, now).toDays() < 20) {
                if (pms[idx].author == user) {
                    pms[idx].recipients.forEach { if (it !in resList) resList.add(it) }
                } else {
                    if (pms[idx].author !in resList) {
                        resList.add(pms[idx].author)
                    }
                }
                idx++
            }
            if (idx > 0) {
                // best priority to first PM
                resList.add(0, resList.removeAt(remainingOpponents.size))
            }
        }
        messageRepository.findTop50ByCategoryNotInOrderByCreatedDesc(setOf(MessageCategory.PRIVATE))
            .forEach { if (it.author !in resList) resList.add(it.author) }
        tournamentService.getCurrentTournament()?.let { currentTournament ->
            applicationRepository.findByTournament(currentTournament)
                .map { it.applicant }
                .forEach { if (it !in resList) resList.add(it) }
        }
        return resList
    }
}
