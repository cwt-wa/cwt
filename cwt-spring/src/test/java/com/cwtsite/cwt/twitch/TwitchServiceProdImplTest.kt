package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.test.MockitoUtils
import org.junit.Test
import org.mockito.*
import kotlin.test.BeforeTest

class TwitchServiceProdImplTest {

    @InjectMocks private lateinit var twitchService: TwitchServiceProdImpl
    @Spy private val twitchProperties: TwitchProperties = TwitchProperties()
    @Mock private lateinit var configurationService: ConfigurationService
    @InjectMocks private lateinit var restTemplateProvider: RestTemplateProvider

    @BeforeTest
    fun constructRestTemplate() {
    }

    @Test
    fun test() {
        restTemplateProvider = Mockito.spy(with(RestTemplateProvider()) { postConstruct(); this })

        MockitoAnnotations.initMocks(this)
        twitchService.postConstruct()

        Mockito.doReturn("successfullyHidThisInformation").`when`(twitchProperties).clientId
        Mockito.doReturn("successfullyHidThisInformation").`when`(twitchProperties).clientSecret

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API))
                .thenReturn(Configuration(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API, null))

        Mockito
                .`when`(configurationService.save(MockitoUtils.anyObject<Configuration>()))
                .thenAnswer { it.getArgument(0) }

        twitchService.requestVideos(listOf("1", "2", "3"))
    }

    @Test
    fun testWithMoockedRestTemplate() {
        // TODO Upcoming
    }
}
