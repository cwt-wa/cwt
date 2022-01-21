package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.user.repository.entity.User

data class UserMinimalDto(
    val id: Long,
    val username: String
) {

    companion object {
        fun toDto(user: User) = UserMinimalDto(user.id!!, user.username)
    }
}
