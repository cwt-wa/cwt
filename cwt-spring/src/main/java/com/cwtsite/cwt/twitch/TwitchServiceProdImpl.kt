package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.core.profile.Prod
import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.twitch.model.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForObject
import java.net.URI
import java.text.SimpleDateFormat
import javax.annotation.PostConstruct

@Prod
@Service
class TwitchServiceProdImpl : TwitchService {

    @Autowired private lateinit var twitchProperties: TwitchProperties
    @Autowired private lateinit var configurationService: ConfigurationService

    private lateinit var restTemplate: RestTemplate
    private var authToken: String? = null
    private val resultLimit = 100

    @PostConstruct
    fun postConstruct() {
        restTemplate = RestTemplateBuilder()
                .interceptors(
                        ClientHttpRequestInterceptor { request, body, execution ->
                            request.headers.add(twitchProperties.authorizationHeaderName!!, "Bearer $authToken")
                            execution.execute(request, body)
                        })
                .messageConverters(
                        with(MappingJackson2HttpMessageConverter()) {
                            supportedMediaTypes = listOf(MediaType.APPLICATION_JSON)
                            objectMapper = with(ObjectMapper()) {
                                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                            }
                            this
                        })
                .build()
    }

    fun authorize() {
        when (authToken) {
            null -> authToken = authenticate()
            else -> if (!validateAuthentication().statusCode.is2xxSuccessful) authToken = authenticate()
        }
    }

    private fun validateAuthentication() = restTemplate
            .exchange<TwitchAuthValidationDto>(
                    RequestEntity(
                            mapOf("${twitchProperties.authorizationHeaderName}" to "OAuth $authToken"),
                            HttpMethod.GET,
                            URI.create("${twitchProperties.authValidateUrl}"))
            )


    private fun authenticate() = restTemplate
            .postForObject(
                    "${twitchProperties.authUrl}" +
                            "?client_id={clientId}" +
                            "&client_secret={clientSecret}" +
                            "&grant_type=client_credentials",
                    null,
                    TwitchAuthDto::class.java,
                    mapOf(
                            "clientId" to twitchProperties.clientId,
                            "clientSecret" to twitchProperties.clientSecret
                    ))
            .accessToken


    override fun requestVideos(channelIds: List<String>): List<TwitchVideoDto> {
        authorize()
        val videosToPaginationCursor = recursivelyRequestNewVideos(
                channelIds,
                configurationService.getOne(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API).value ?: "")
        configurationService.save(Configuration(ConfigurationKey.PAGINATION_CURSOR_VIDEOS_TWITCH_API, videosToPaginationCursor.second))
        return videosToPaginationCursor.first
    }

    private fun recursivelyRequestNewVideos(
            channelIds: List<String>,
            paginationCursor: String,
            videos: MutableList<TwitchVideoDto> = mutableListOf()): Pair<List<TwitchVideoDto>, String> {
        val res = restTemplate.getForObject<TwitchWrappedDto<TwitchVideoDto>>(
                "${twitchProperties.url}/${twitchProperties.videosEndpoint!!}?first=$resultLimit&after=$paginationCursor${channelIds.joinToString { "&user_id=$it" }}",
                TwitchWrappedDto::class.java)!!

        videos.addAll(res.data)

        if (res.data.size == resultLimit) {
            val videosToCursor = recursivelyRequestNewVideos(channelIds, res.pagination.cursor, videos)
            return Pair(videosToCursor.first, videosToCursor.second)
        }

        return Pair(videos, res.pagination.cursor)
    }

    override fun requestStreams(): List<TwitchStreamDto> {
        authorize()
        return restTemplate.getForObject<TwitchWrappedDto<TwitchStreamDto>>(
                "${twitchProperties.url}/${twitchProperties.streamsEndpoint!!}",
                TwitchWrappedDto::class.java)!!.data
    }
}
