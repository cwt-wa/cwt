package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.user.repository.entity.User

@DataTransferObject
data class UserOverviewDto(
    val id: Long,
    val username: String,
    val country: CountryDto,
    val participations: Int,
    val userStats: List<UserStatsDto>
) {

    companion object {

        fun toDto(user: User, userStatsDtos: List<UserStatsDto>): UserOverviewDto = UserOverviewDto(
            id = user.id!!,
            username = user.username,
            country = CountryDto.toDto(user.country),
            participations = user.userStats?.participations ?: 0,
            userStats = userStatsDtos
        )
    }
}
