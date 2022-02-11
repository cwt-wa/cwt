package com.cwtsite.cwt.domain.configuration.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class ConfigurationServiceTest {

    @InjectMocks private lateinit var cut: ConfigurationService
    @Mock private lateinit var configurationRepository: ConfigurationRepository
    @Mock private lateinit var tournamentService: TournamentService

    @Test
    fun save() {
        val tournament = EntityDefaults.tournament()
        `when`(tournamentService.getCurrentTournament()).thenReturn(tournament)
        val configuration = Configuration(
            key = ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING,
            value = "2"
        )
        `when`(tournamentService.save(anyObject()))
            .thenAnswer {
                val value = Integer.parseInt(configuration.value)
                assertThat(it.getArgument<Tournament>(0).numOfGroupAdvancing).isEqualTo(value)
                tournament.copy(numOfGroupAdvancing = value)
            }
        cut.save(configuration)
        verify(tournamentService).save(tournament)
    }
}
