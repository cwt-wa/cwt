package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.user.repository.entity.User

data class UserDetailDto(
        val id: Long,
        val username: String,
        val country: CountryDto,
        val about: String?,
        val hasPic: Boolean,
        val userStats: List<UserStatsDto>,
        val email: String?
) 

