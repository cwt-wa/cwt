package com.cwtsite.cwt.domain.configuration.view.model

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.repository.entity.User

@DataTransferObject
data class ConfigurationDto(
        val value: String,
        val key: ConfigurationKey
) {

    companion object {

        fun fromDto(configurationDto: ConfigurationDto, author: User) =
                Configuration(
                        key = configurationDto.key,
                        value = configurationDto.value,
                        author = author
                )
    }
}
