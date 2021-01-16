package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.core.profile.Integration
import com.cwtsite.cwt.core.profile.Prod
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.twitch.model.TwitchStreamDto
import com.cwtsite.cwt.twitch.view.model.TwitchUserDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Prod
@Integration
@Primary
@Service
class TwitchServiceProdImpl : TwitchService {

    override var lastVideosRequest: LocalDateTime? = null
    override var lastStreamsRequest: LocalDateTime? = null

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var restTemplateProvider: RestTemplateProvider

    @PostConstruct
    fun postConstruct() {
        restTemplateProvider.addAuthTokenHeaderInterceptor()
    }

    private fun authorize() {
        when (restTemplateProvider.authToken) {
            null -> restTemplateProvider.authToken = authenticate()
            else -> {
                try {
                    validateAuthentication()
                } catch (e: Exception) {
                    restTemplateProvider.authToken = authenticate()
                }
            }
        }
    }

    private fun validateAuthentication() = restTemplateProvider.validateAuthentication()

    private fun authenticate() = restTemplateProvider.authenticate()

    override fun requestVideo(videoId: String): TwitchVideoDto? {
        authorize()
        return restTemplateProvider.fetchVideo(videoId)
    }

    override fun requestVideos(channels: List<Channel>): List<TwitchVideoDto> {
        if (channels.isEmpty()) return emptyList()
        authorize()

        return channels
                .map {
                    val videosToPaginationCursor = recursivelyRequestNewVideos(it.id, it.videoCursor ?: "")
                    streamService.saveVideoCursor(it, videosToPaginationCursor.second)
                    lastVideosRequest = LocalDateTime.now()
                    return@map videosToPaginationCursor.first
                }
                .flatten()
    }

    private fun recursivelyRequestNewVideos(
            channelId: String,
            paginationCursor: String,
            videos: MutableList<TwitchVideoDto> = mutableListOf()): Pair<List<TwitchVideoDto>, String?> {
        val res = restTemplateProvider.fetchVideos(paginationCursor, channelId)

        videos.addAll(res.data)

        if (res.data.size == restTemplateProvider.resultLimit) {
            val videosToCursor = recursivelyRequestNewVideos(channelId, res.pagination!!.cursor ?: "", videos)
            return Pair(videosToCursor.first, videosToCursor.second)
        }

        return Pair(videos, res.pagination!!.cursor)
    }

    override fun requestStreams(channelIds: List<String>): List<TwitchStreamDto> {
        authorize()
        val streams = restTemplateProvider.fetchStreams(channelIds)
        lastStreamsRequest = LocalDateTime.now()
        return streams
    }

    override fun requestUsers(vararg loginNames: String): List<TwitchUserDto> {
        if (loginNames.isEmpty()) return emptyList()
        authorize()
        return restTemplateProvider.fetchUsers(*loginNames)
    }
}
