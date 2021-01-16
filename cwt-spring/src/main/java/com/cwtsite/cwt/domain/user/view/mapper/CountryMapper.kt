package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.view.model.CountryDto
import com.cwtsite.cwt.domain.user.repository.entity.Country

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class CountryMapper {

    fun toDto(country: Country) = CountryDto(
            id = country.id!!,
            name = country.name,
            flag = country.flag
    )
}

