package com.cwtsite.cwt.domain.configuration.view.mapper

import com.cwtsite.cwt.domain.configuration.view.model.ConfigurationDto
import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.user.repository.entity.User

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class ConfigurationMapper {

    fun fromDto(configurationDto: ConfigurationDto, author: User) =
            Configuration(
                    key = configurationDto.key,
                    value = configurationDto.value,
                    author = author
            )
}
