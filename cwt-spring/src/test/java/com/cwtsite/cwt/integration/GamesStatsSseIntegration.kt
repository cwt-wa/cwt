package com.cwtsite.cwt.integration

import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.Test

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GamesStatsSseIntegration {


    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `existing data is guaranteed to be sent`() {

    }

    @Test
    fun `data is sent upon the according application event`() {

    }

    @Test
    fun `existing data is sent when we're past the timeout and stream is then immediately closed`() {

    }

    @Test
    fun `subscriptions are closed once the emitter completes`() {

    }
}
