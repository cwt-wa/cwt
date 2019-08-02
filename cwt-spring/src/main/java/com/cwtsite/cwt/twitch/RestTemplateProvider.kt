package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.twitch.model.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForObject
import java.net.URI
import java.text.SimpleDateFormat
import javax.annotation.PostConstruct

@Service
class RestTemplateProvider {

    val resultLimit: Int get() = twitchProperties.resultLimit!!
    var authToken: String? = null

    private lateinit var restTemplate: RestTemplate

    @Autowired private lateinit var twitchProperties: TwitchProperties

    @PostConstruct
    fun postConstruct() {
        restTemplate = RestTemplateBuilder()
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


    fun addAuthTokenHeaderInterceptor() {
        restTemplate.interceptors.add(
                ClientHttpRequestInterceptor { request, body, execution ->
                    request.headers.add(twitchProperties.authorizationHeaderName!!, "Bearer $authToken")
                    execution.execute(request, body)
                })
    }

    fun validateAuthentication(): ResponseEntity<TwitchAuthValidationDto> = restTemplate
            .exchange(
                    RequestEntity(
                            mapOf("${twitchProperties.authorizationHeaderName}" to "OAuth $authToken"),
                            HttpMethod.GET,
                            URI.create("${twitchProperties.authValidateUrl}"))
            )

    fun authenticate(): String? = restTemplate
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


    fun fetchVideos(paginationCursor: String, channelIds: List<String>): TwitchWrappedDto<TwitchVideoDto>? =
            restTemplate.getForObject<TwitchWrappedDto<TwitchVideoDto>>(
                    "${twitchProperties.url}/${twitchProperties.videosEndpoint}?first=${twitchProperties.resultLimit}&after=$paginationCursor${channelIds.joinToString(separator = "") { "&user_id=$it" }}",
                    TwitchWrappedDto::class.java)

    fun fetchStreams(): List<TwitchStreamDto> =
            restTemplate.getForObject<TwitchWrappedDto<TwitchStreamDto>>(
                    "${twitchProperties.url}/${twitchProperties.streamsEndpoint}",
                    TwitchWrappedDto::class.java)!!.data

    fun fetchUsers(vararg loginNames: String): List<TwitchUserDto> {
        return restTemplate.getForObject<TwitchWrappedDto<TwitchUserDto>>(
                "${twitchProperties.url}/${twitchProperties.usersEndpoint}",
                TwitchWrappedDto::class.java)!!.data

    }

}
