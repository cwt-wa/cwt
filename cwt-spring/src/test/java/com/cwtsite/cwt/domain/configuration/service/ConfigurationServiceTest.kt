package com.cwtsite.cwt.domain.configuration.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.test.EntityDefaults
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ConfigurationServiceTest {

    @InjectMocks
    private lateinit var configurationService: ConfigurationService

    @Mock
    private lateinit var configurationRepository: ConfigurationRepository

    @Test
    fun getParsedPointsPatternConfiguration() {
        Mockito
                .`when`(configurationRepository.findById(ConfigurationKey.POINTS_PATTERN))
                .thenReturn(Optional.of(createPointsPatternConfiguration("(3, 3), (2, 1)")))
                .thenReturn(Optional.of(createPointsPatternConfiguration("(10, 12),(0, 1),(2  , 1), (0,0),")))

        Assert.assertArrayEquals(
                arrayOf(intArrayOf(3, 3), intArrayOf(2, 1)),
                configurationService.parsedPointsPatternConfiguration.toTypedArray())

        Assert.assertArrayEquals(
                arrayOf(intArrayOf(10, 12), intArrayOf(0, 1), intArrayOf(2, 1)),
                configurationService.parsedPointsPatternConfiguration.toTypedArray())
    }

    private fun createPointsPatternConfiguration(value: String) =
            Configuration(
                    value = value,
                    key = ConfigurationKey.POINTS_PATTERN,
                    author = EntityDefaults.user()
            )
}
