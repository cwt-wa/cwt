package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import com.cwtsite.cwt.twitch.model.TwitchPaginationDto
import com.cwtsite.cwt.twitch.model.TwitchUserDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import com.cwtsite.cwt.twitch.model.TwitchWrappedDto
import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.*
import kotlin.math.roundToInt
import kotlin.test.Ignore

class TwitchServiceProdImplTest {

    @InjectMocks private lateinit var twitchService: TwitchServiceProdImpl
    @Spy private val twitchProperties: TwitchProperties = TwitchProperties()
    @Mock private lateinit var configurationService: ConfigurationService
    @InjectMocks private lateinit var restTemplateProvider: RestTemplateProvider

    @Test
    @Ignore("Performs actual not mocked request to the Twitch API")
    fun integrationTestVideoRequest() {
        setupIntegrationTest()

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API))
                .thenReturn(Configuration(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API, null))

        Mockito
                .`when`(configurationService.save(MockitoUtils.anyObject<Configuration>()))
                .thenAnswer { it.getArgument(0) }

        twitchService.requestVideos(listOf("26027047"))
                .map {
                    StreamDto.toDto(it, com.cwtsite.cwt.domain.stream.entity.Channel(
                            id = "26027047",
                            user = EntityDefaults.user(),
                            title = "GloriousTV"
                    ))
                }
    }

    @Test
    @Ignore("Performs actual not mocked request to the Twitch API")
    fun integrationTestUserRequest() {
        setupIntegrationTest()
        twitchService.requestUsers("khamski", "mrtpenguin")
                .map { TwitchUserDto.fromDto(it, EntityDefaults.user(), "EpicTV") }
    }

    private fun setupIntegrationTest() {
        restTemplateProvider = Mockito.spy(with(RestTemplateProvider()) { postConstruct(); this })

        MockitoAnnotations.initMocks(this)
        twitchService.postConstruct()

        Mockito.doReturn("yourClientId").`when`(twitchProperties).clientId
        Mockito.doReturn("yourClientSecret").`when`(twitchProperties).clientSecret
    }

    @Test
    fun `two pages of results`() {
        restTemplateProvider = Mockito.mock(RestTemplateProvider::class.java)

        MockitoAnnotations.initMocks(this)
        twitchService.postConstruct()

        Mockito.`when`(restTemplateProvider.authToken).thenReturn(null)
        Mockito.`when`(restTemplateProvider.authenticate()).thenReturn("thisIsTheAuthToken")
        Mockito.`when`(restTemplateProvider.resultLimit).thenReturn(2)

        Mockito.doReturn("schokoClientId").`when`(twitchProperties).clientId
        Mockito.doReturn("schokoClientSecret").`when`(twitchProperties).clientSecret

        val paginationCursorValues = listOf("thisIsPaginationCursor1", "thisIsPaginationCursor2")
        val channels = listOf(
                object : Channel {
                    override val id = "372891"
                    override val username = "KhamsTV"
                },
                object : Channel {
                    override val id = "5304627"
                    override val username = "Delucia"
                })
        val expectedVideos = listOf(
                createVideoDto(channels[0], "The First Video"),
                createVideoDto(channels[1], "The Second Video"),
                createVideoDto(channels[0], "The Third And Last Video")
        )

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API))
                .thenReturn(Configuration(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API, null))

        Mockito
                .`when`(restTemplateProvider.fetchVideos(Mockito.anyString(), Mockito.anyList<String>()))
                .thenAnswer {
                    val paginationCursorArg = it.getArgument<String>(0)
                    val channelIdsArg = it.getArgument<List<String>>(1)

                    Assertions.assertThat(paginationCursorArg).isEmpty()
                    Assertions.assertThat(channelIdsArg).containsExactlyInAnyOrder(*channels.map { c -> c.id }.toTypedArray())

                    return@thenAnswer TwitchWrappedDto(
                            data = listOf(
                                    expectedVideos[0],
                                    expectedVideos[1]
                            ),
                            pagination = TwitchPaginationDto(paginationCursorValues[0])
                    )
                }
                .thenAnswer {
                    val paginationCursorArg = it.getArgument<String>(0)
                    val channelIdsArg = it.getArgument<List<String>>(1)

                    Assertions.assertThat(paginationCursorArg).isEqualTo(paginationCursorValues[0])
                    Assertions.assertThat(channelIdsArg).containsExactlyInAnyOrder(*channels.map { c -> c.id }.toTypedArray())

                    return@thenAnswer TwitchWrappedDto(
                            data = listOf(
                                    expectedVideos[2]
                            ),
                            pagination = TwitchPaginationDto(paginationCursorValues[1])
                    )
                }

        Mockito
                .`when`(configurationService.save(MockitoUtils.anyObject<Configuration>()))
                .thenAnswer {
                    val paginationCursorArg = it.getArgument<Configuration>(0)
                    Assertions.assertThat(paginationCursorArg.value).isEqualTo(paginationCursorValues[1])
                    return@thenAnswer paginationCursorArg
                }

        Assertions
                .assertThat(twitchService.requestVideos(channels.map { it.id }))
                .containsExactlyInAnyOrder(*expectedVideos.toTypedArray())
    }

    private fun createVideoDto(channel: Channel, videoTitle: String) = TwitchVideoDto(
            id = Math.random().times(1000).roundToInt().toString(),
            userId = channel.id,
            userName = channel.username,
            title = videoTitle,
            description = "Video Description",
            createdAt = "2019-07-21T14:25:59Z",
            publishedAt = "2019-07-21T14:25:59Z",
            url = "https://www.twitch.tv/videos/234482848",
            thumbnailUrl = "https://static-cdn.jtvnw.net/s3_vods/bebc8cba2926d1967418_chewiemelodies_27786761696_805342775/thumb/thumb0-%{width}x%{height}.jpg",
            viewable = "public",
            viewCount = 142,
            language = "en",
            type = "archive",
            duration = "3h8m33s"
    )
}

interface Channel {
    val id: String;
    val username: String
}
