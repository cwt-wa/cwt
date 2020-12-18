package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.twitch.model.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import javax.annotation.PostConstruct

@Service
class RestTemplateProvider {

    val resultLimit: Int get() = twitchProperties.resultLimit!!
    var authToken: String? = null

    private lateinit var restTemplate: RestTemplate

    @Autowired private lateinit var twitchProperties: TwitchProperties

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun postConstruct() {
        restTemplate = RestTemplateBuilder()
                .requestFactory { BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()) }
                .messageConverters(
                        with(MappingJackson2HttpMessageConverter()) {
                            supportedMediaTypes = listOf(MediaType.APPLICATION_JSON)
                            objectMapper = with(ObjectMapper()) {
                                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                            }
                            this
                        })
                .interceptors(ClientHttpRequestInterceptor { request, body, execution ->
                    logger.info("Adding client-id header: ${twitchProperties.clientId}")
                    request.headers.add("client-id", twitchProperties.clientId!!)
                    execution.execute(request, body)
                }, ClientHttpRequestInterceptor { request, body, execution ->
                    logger.info("""
                            method: ${request.method} 
                            uri: ${request.uri} 
                            headers: ${request.headers} 
                            body: ${body.toString(Charset.defaultCharset())}""".trimIndent())
                    val response = execution.execute(request, body)
                    logger.info("""
                            statusCode: ${response.statusCode}
                            headers: ${response.headers} 
                            body: ${response.body.bufferedReader().use { it.readText() }}""".trimIndent())
                    response
                })
                .build()
    }


    fun addAuthTokenHeaderInterceptor() {
        restTemplate.interceptors.add(
                ClientHttpRequestInterceptor { request, body, execution ->
                    logger.info("Adding ${twitchProperties.authorizationHeaderName} header: $authToken")
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
                    ))!!
            .accessToken

    fun fetchVideo(videoId: String): TwitchVideoDto? {
        var response = restTemplate.exchange(
                RequestEntity<TwitchWrappedDto<TwitchVideoDto>>(
                        HttpMethod.GET,
                        URI.create("${twitchProperties.url}${twitchProperties.videosEndpoint}?first=1&id=$videoId")),
                object : ParameterizedTypeReference<TwitchWrappedDto<TwitchVideoDto>>() {}).body!!
        if (response.data.isEmpty()) {
            return null
        }
        return response.data.get(0)
    }

    fun fetchVideos(paginationCursor: String, channelId: String): TwitchWrappedDto<TwitchVideoDto> =
            restTemplate.exchange(
                    RequestEntity<TwitchWrappedDto<TwitchVideoDto>>(
                            HttpMethod.GET,
                            URI.create("${twitchProperties.url}${twitchProperties.videosEndpoint}" +
                                    "?first=${twitchProperties.resultLimit}" +
                                    "&after=$paginationCursor" +
                                    "&user_id=$channelId")),
                    object : ParameterizedTypeReference<TwitchWrappedDto<TwitchVideoDto>>() {}).body!!

    fun fetchStreams(channelIds: List<String>): List<TwitchStreamDto> =
            restTemplate.exchange(
                    RequestEntity<TwitchWrappedDto<TwitchStreamDto>>(
                            HttpMethod.GET,
                            URI.create("${twitchProperties.url}${twitchProperties.streamsEndpoint}" +
                                    channelIds.joinToString(separator = "") { "&user_id=$it" })),
                    object : ParameterizedTypeReference<TwitchWrappedDto<TwitchStreamDto>>() {}).body!!.data

    fun fetchUsers(vararg loginNames: String): List<TwitchUserDto> {
        return restTemplate.exchange(
                RequestEntity<TwitchWrappedDto<TwitchUserDto>>(
                        HttpMethod.GET,
                        URI.create("${twitchProperties.url}${twitchProperties.usersEndpoint}" +
                                "?${loginNames.joinToString(separator = "") { "&login=$it" }}")),
                object : ParameterizedTypeReference<TwitchWrappedDto<TwitchUserDto>>() {}).body!!.data
    }
}
