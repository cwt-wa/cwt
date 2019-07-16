package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.test.MockitoUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TwitchServiceProdImplTest {

    @InjectMocks private lateinit var twitchService: TwitchServiceProdImpl
    @Spy private val twitchProperties: TwitchProperties = TwitchProperties()
    @Mock private lateinit var configurationService: ConfigurationService

    @Test
    fun test() {
        Mockito.doReturn("successfullyHidThisInformation").`when`(twitchProperties).clientId
        Mockito.doReturn("successfullyHidThisInformation").`when`(twitchProperties).clientSecret

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API))
                .thenReturn(Configuration(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API, null))

        Mockito
                .`when`(configurationService.save(MockitoUtils.anyObject<Configuration>()))
                .thenAnswer { it.getArgument(0) }

        twitchService.postConstruct()
        twitchService.requestVideos(listOf("1", "2", "3"))
    }
}
