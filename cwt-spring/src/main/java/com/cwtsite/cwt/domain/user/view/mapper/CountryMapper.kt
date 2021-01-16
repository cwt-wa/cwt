package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.repository.entity.Country
import com.cwtsite.cwt.domain.user.view.model.CountryDto
import org.springframework.stereotype.Component

@Component
class CountryMapper {

    fun toDto(country: Country) = CountryDto(
        id = country.id!!,
        name = country.name,
        flag = country.flag
    )
}
