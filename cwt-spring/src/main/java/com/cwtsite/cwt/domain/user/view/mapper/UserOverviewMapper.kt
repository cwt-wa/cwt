package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.view.model.UserOverviewDto
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.view.mapper.CountryMapper
import com.cwtsite.cwt.domain.user.view.model.UserStatsDto
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class UserOverviewMapper {

    @Autowired private lateinit var countryMapper: CountryMapper

    fun toDto(user: User, userStatsDtos: List<UserStatsDto>): UserOverviewDto = UserOverviewDto(
            id = user.id!!,
            username = user.username,
            country = countryMapper.toDto(user.country),
            participations = user.userStats?.participations ?: 0,
            userStats = userStatsDtos
    )
}

