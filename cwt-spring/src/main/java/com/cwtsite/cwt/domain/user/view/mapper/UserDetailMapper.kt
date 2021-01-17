package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.view.model.UserDetailDto
import com.cwtsite.cwt.domain.user.view.model.UserStatsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
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
