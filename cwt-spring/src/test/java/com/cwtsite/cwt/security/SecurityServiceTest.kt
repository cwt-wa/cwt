package com.cwtsite.cwt.security

import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.util.ReflectionTestUtils

@RunWith(MockitoJUnitRunner::class)
class SecurityServiceTest {

    @Mock private lateinit var securityService: SecurityService;
    private val captchaSecret: String = ""
    private val wormnetChannel: String = "chocolate-chimpanze,chocolatechimpanze,chocolate chimpanze"

    @Test
    fun verifySecretWord() {
        ReflectionTestUtils.setField(securityService, "captchaSecret", captchaSecret)
        ReflectionTestUtils.setField(securityService, "wormnetChannel", wormnetChannel)

        Mockito.`when`(securityService.verifySecretWord(Mockito.anyString())).thenCallRealMethod()

        Assertions.assertThat(securityService.verifySecretWord("#chocolatechimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("chocolatechimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("chocolate chimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("#chocolate-chimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("chocolate-chimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("#chocolate chimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("chocolateChimpanze")).isTrue()
        Assertions.assertThat(securityService.verifySecretWord("candyApe")).isFalse()
    }
}
