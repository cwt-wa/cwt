package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.repository.entity.Country

@DataTransferObject
data class CountryDto(
        val id: Long,
        val name: String,
        val flag: String
) {

    companion object {

        fun toDto(country: Country) = CountryDto(
                id = country.id!!,
                name = country.name,
                flag = country.flag
        )
    }
}
