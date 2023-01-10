package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.test.EntityDefaults.game
import com.cwtsite.cwt.test.EntityDefaults.user
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RankingServiceTest {

    @InjectMocks
    private lateinit var rankingService: RankingService

    @Mock
    private lateinit var gameRepository: GameRepository

    @Test
    fun testRelrank() {
        `when`(gameRepository.findAll())
            .thenReturn(
                listOf(
                    game(
                        homeUser = user(id = 1),
                        awayUser = user(id = 2),
                        scoreHome = 1,
                        scoreAway = 2,
                    ),
                    game(
                        homeUser = user(id = 3),
                        awayUser = user(id = 4),
                        scoreHome = 4,
                        scoreAway = 1,
                    ),
                )
            )
        val res = rankingService.relrank()
        assertThat(res).hasSize(4)
        assertThat(res.map { it.first }).containsExactly(1, 2, 3, 4)
    }
}
