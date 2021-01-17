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
) 

