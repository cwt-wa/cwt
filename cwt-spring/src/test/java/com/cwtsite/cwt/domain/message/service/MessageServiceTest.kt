package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.application.service.ApplicationRepository
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.test.EntityDefaults.message
import com.cwtsite.cwt.test.EntityDefaults.user
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.Instant
import java.time.Period.ofDays
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class MessageServiceTest {

    @InjectMocks private lateinit var messageService: MessageService
    @Mock private lateinit var userService: UserService
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var messageRepository: MessageRepository
    @Mock private lateinit var messageEventListener: MessageEventListener
    @Mock private lateinit var applicationRepository: ApplicationRepository

    @Test
    fun genSuggestions() {
        val reqUser = user()
        assertThat(reqUser.id).isEqualTo(1)

        val remOpp = listOf(user(id = 2), user(id = 3))
        `when`(userService.getRemainingOpponents(reqUser)).thenReturn(remOpp)
        `when`(tournamentService.getCurrentTournament()).thenReturn(null)

        val pms = listOf(
            message(
                id = 4,
                author = user(id = 13),
                category = MessageCategory.PRIVATE,
                recipients = mutableListOf(reqUser)
            ),
            message(
                id = 5,
                author = user(id = 15),
                created = Instant.now().minus(ofDays(40)),
                category = MessageCategory.PRIVATE,
                recipients = mutableListOf(reqUser)
            ),
        )
        val messages = (10..61).map { message(id = it.toLong(), author = user(id = 20)) }
        `when`(messageRepository.findPrivateMessages(reqUser)).thenReturn(pms)
        `when`(messageRepository.findTop50ByCategoryNotInOrderByCreatedDesc(setOf(MessageCategory.PRIVATE)))
            .thenReturn(messages)

        val expSuggs = listOf(
            pms[0].author,
            *remOpp.toTypedArray(),
            *messages.map { it.author }.toTypedArray()
        )
        assertThat(messageService.genSuggestions(reqUser))
            .containsExactly(*expSuggs.distinct().toTypedArray())
    }
}
