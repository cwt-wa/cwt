package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import test.AbstractDbTest

open class ConfigurationRepositoryTest : AbstractDbTest() {

    @Autowired
    private lateinit var configurationRepository: ConfigurationRepository

    @Test
    fun testFindAll() {
        val configurations = configurationRepository.findAll()
        val configurationKeys = ConfigurationKey.values()

        for (configurationKey in configurationKeys) {
            val configuration = configurations
                    .find { it.key == configurationKey } ?: throw RuntimeException()

            parseValue(configuration.value!!, configuration.key.type)
        }
    }

    private fun parseValue(value: String, type: ConfigurationKey.ConfigurationValueType) {
        @Suppress("UNUSED_EXPRESSION")
        when (type) {
            ConfigurationKey.ConfigurationValueType.INTEGER -> Integer.parseInt(value)
            ConfigurationKey.ConfigurationValueType.STRING -> value
        }
    }

}
