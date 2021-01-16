package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.view.model.UserDetailDto
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.view.model.UserStatsDto
import com.cwtsite.cwt.domain.user.view.mapper.CountryMapper
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class UserDetailMapper {

    @Autowired private lateinit var countryMapper: CountryMapper

    fun toDto(user: User, userStatsDtos: List<UserStatsDto>): UserDetailDto = UserDetailDto(
            id = user.id!!,
            username = user.username,
            country = countryMapper.toDto(user.country),
            hasPic = user.photo != null,
            about = user.about,
            userStats = userStatsDtos,
            email = user.email
    )
}

