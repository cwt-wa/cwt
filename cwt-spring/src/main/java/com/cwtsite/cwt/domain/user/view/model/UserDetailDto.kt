package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.user.repository.entity.User

data class UserDetailDto(
        val id: Long,
        val username: String,
        val country: CountryDto,
        val about: String?,
        val hasPic: Boolean,
        val userStats: List<UserStatsDto>
) {

    companion object {

        fun toDto(user: User, userStatsDtos: List<UserStatsDto>): UserDetailDto = UserDetailDto(
                id = user.id!!,
                username = user.username,
                country = CountryDto.toDto(user.country),
                hasPic = false,
                about = user.about,
                userStats = userStatsDtos
        )
    }
}
