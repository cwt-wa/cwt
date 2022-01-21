package com.cwtsite.cwt.database

import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository
import com.cwtsite.cwt.integration.EmbeddedPostgres
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@EmbeddedPostgres
class ConfigurationRepositoryTest {

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
            ConfigurationKey.ConfigurationValueType.BOOLEAN -> value.toBoolean()
        }
    }
}
