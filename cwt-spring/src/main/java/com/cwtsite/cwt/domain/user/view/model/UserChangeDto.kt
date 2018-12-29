package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.user.repository.entity.User

data class UserChangeDto(
        val username: String?,
        val country: String?,
        val about: String?) {

    companion object {

        fun toDto(user: User) = UserChangeDto(
                username = user.username,
                country = user.country,
                about = user.about
        )
    }
}