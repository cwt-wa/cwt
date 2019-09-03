package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.service.StreamService
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
    @Mock private lateinit var streamService: StreamService
    @InjectMocks private lateinit var restTemplateProvider: RestTemplateProvider

    @Test
    @Ignore("Performs actual not mocked request to the Twitch API")
    fun integrationTestVideoRequest() {
        setupIntegrationTest()

        Mockito
                .`when`(streamService.saveVideoCursor(MockitoUtils.anyObject<Channel>(), Mockito.anyString()))
                .thenAnswer { it.getArgument(0) }

        val channels = listOf(
                EntityDefaults.channel("26027047", "Khamski"),
                EntityDefaults.channel("25468719", "DarkOne"))

        twitchService.requestVideos(channels)
                .map { StreamDto.toDto(it, channels.find { c -> c.id == it.id }!!) }
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

        val paginationCursorValues = listOf(
                "thisIsPaginationCursor1",
                "thisIsPaginationCursor2",
                "thisIsPaginationCursor3")
        val channels = listOf(
                Mockito.spy(EntityDefaults.channel("26027047", "Khamski")),
                Mockito.spy(EntityDefaults.channel("25468719", "DarkOne")))
        val expectedVideos = listOf(
                createVideoDto(channels[0], "The First Video"),
                createVideoDto(channels[1], "The Second Video"),
                createVideoDto(channels[0], "The Third Video"),
                createVideoDto(channels[0], "The Fourth And Last Video")
        )

        Mockito
                .`when`(restTemplateProvider.fetchVideos(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer {
                    val paginationCursorArg = it.getArgument<String>(0)
                    val channelIdArg = it.getArgument<String>(1)

                    Assertions.assertThat(paginationCursorArg).isEmpty()
                    Assertions.assertThat(channelIdArg).isEqualTo(channels[0].id)

                    return@thenAnswer TwitchWrappedDto(
                            data = listOf(
                                    expectedVideos[0],
                                    expectedVideos[2]
                            ),
                            pagination = TwitchPaginationDto(paginationCursorValues[0])
                    )
                }
                .thenAnswer {
                    val paginationCursorArg = it.getArgument<String>(0)
                    val channelIdArg = it.getArgument<String>(1)

                    Assertions.assertThat(paginationCursorArg).isEqualTo(paginationCursorValues[0])
                    Assertions.assertThat(channelIdArg).isEqualTo(channels[0].id)

                    return@thenAnswer TwitchWrappedDto(
                            data = listOf(
                                    expectedVideos[3]
                            ),
                            pagination = TwitchPaginationDto(paginationCursorValues[1])
                    )
                }
                .thenAnswer {
                    val paginationCursorArg = it.getArgument<String>(0)
                    val channelIdArg = it.getArgument<String>(1)

                    Assertions.assertThat(paginationCursorArg).isEmpty()
                    Assertions.assertThat(channelIdArg).isEqualTo(channels[1].id)

                    return@thenAnswer TwitchWrappedDto(
                            data = listOf(
                                    expectedVideos[1]
                            ),
                            pagination = TwitchPaginationDto(paginationCursorValues[2])
                    )
                }

        Mockito
                .`when`(streamService.saveVideoCursor(MockitoUtils.anyObject(), Mockito.anyString()))
                .thenAnswer {
                    val channelArg = it.getArgument<Channel>(0)

                    Assertions.assertThat(channelArg).isEqualTo(channels[0])
                    Assertions.assertThat(it.getArgument<String>(1)).isEqualTo(paginationCursorValues[1])

                    channels[0].videoCursor = paginationCursorValues[1]
                    channelArg.videoCursor = paginationCursorValues[1]

                    return@thenAnswer channelArg
                }
                .thenAnswer {
                    val channelArg = it.getArgument<Channel>(0)

                    Assertions.assertThat(channelArg).isEqualTo(channels[1])
                    Assertions.assertThat(it.getArgument<String>(1)).isEqualTo(paginationCursorValues[2])

                    channels[0].videoCursor = paginationCursorValues[2]
                    channelArg.videoCursor = paginationCursorValues[2]

                    return@thenAnswer channelArg
                }

        val actualVideos = twitchService.requestVideos(channels)

        Mockito
                .verify(restTemplateProvider, Mockito.times(3))
                .fetchVideos(Mockito.anyString(), Mockito.anyString())

        Mockito
                .verify(streamService, Mockito.times(2))
                .saveVideoCursor(MockitoUtils.anyObject(), MockitoUtils.anyObject())

        Assertions
                .assertThat(actualVideos)
                .containsExactlyInAnyOrder(*expectedVideos.toTypedArray())
    }

    private fun createVideoDto(channel: Channel, videoTitle: String) = TwitchVideoDto(
            id = Math.random().times(1000).roundToInt().toString(),
            userId = channel.id,
            userName = channel.displayName,
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

