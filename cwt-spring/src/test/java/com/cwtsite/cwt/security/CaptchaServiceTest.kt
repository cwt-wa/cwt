package com.cwtsite.cwt.security

import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.internal.util.reflection.FieldSetter
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.test.util.ReflectionTestUtils

@RunWith(MockitoJUnitRunner::class)
class CaptchaServiceTest {

    @Mock private lateinit var captchaService: CaptchaService;
    private val captchaSecret: String = ""
    private val wormnetChannel: String = "chocolate-chimpanze,chocolatechimpanze,chocolate chimpanze"

    @Test
    fun verifySecretWord() {
        ReflectionTestUtils.setField(captchaService, "captchaSecret", captchaSecret)
        ReflectionTestUtils.setField(captchaService, "wormnetChannel", wormnetChannel)

        Mockito.`when`(captchaService.verifySecretWord(Mockito.anyString())).thenCallRealMethod()

        Assertions.assertThat(captchaService.verifySecretWord("#chocolatechimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("chocolatechimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("chocolate chimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("#chocolate-chimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("chocolate-chimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("#chocolate chimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("chocolateChimpanze")).isTrue()
        Assertions.assertThat(captchaService.verifySecretWord("candyApe")).isFalse()
    }
}
