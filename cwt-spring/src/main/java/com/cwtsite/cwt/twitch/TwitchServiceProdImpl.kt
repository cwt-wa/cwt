package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.core.profile.Prod
import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.twitch.model.TwitchStreamDto
import com.cwtsite.cwt.twitch.model.TwitchUserDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Prod
@Service
class TwitchServiceProdImpl : TwitchService {

    override var lastVideosRequest: LocalDateTime? = null
    override var lastStreamsRequest: LocalDateTime? = null

    @Autowired private lateinit var configurationService: ConfigurationService
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

    override fun requestVideos(channelIds: List<String>): List<TwitchVideoDto> {
        authorize()
        val videosToPaginationCursor = recursivelyRequestNewVideos(
                channelIds,
                configurationService.getOne(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API).value ?: "")
        configurationService.save(Configuration(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API, videosToPaginationCursor.second))
        lastVideosRequest = LocalDateTime.now()
        return videosToPaginationCursor.first
    }

    private fun recursivelyRequestNewVideos(
            channelIds: List<String>,
            paginationCursor: String,
            videos: MutableList<TwitchVideoDto> = mutableListOf()): Pair<List<TwitchVideoDto>, String> {
        val res = restTemplateProvider.fetchVideos(paginationCursor, channelIds)!!

        videos.addAll(res.data)

        if (res.data.size == restTemplateProvider.resultLimit) {
            val videosToCursor = recursivelyRequestNewVideos(channelIds, res.pagination.cursor, videos)
            return Pair(videosToCursor.first, videosToCursor.second)
        }

        return Pair(videos, res.pagination.cursor)
    }

    override fun requestStreams(): List<TwitchStreamDto> {
        authorize()
        val streams = restTemplateProvider.fetchStreams()
        lastStreamsRequest = LocalDateTime.now()
        return streams
    }

    override fun requestUsers(vararg loginNames: String): List<TwitchUserDto> {
        authorize()
        return restTemplateProvider.fetchUsers(*loginNames)
    }
}
