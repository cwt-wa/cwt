package com.cwtsite.cwt.twitch

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import javax.annotation.PostConstruct

@Service
class RestTemplateProvider {

    lateinit var restTemplate: RestTemplate

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
}
