package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.view.model.UserChangeDto
import com.cwtsite.cwt.domain.user.repository.entity.User

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class UserChangeMapper {

    fun toDto(user: User) = UserChangeDto(
            username = user.username,
            country = user.country.id,
            about = user.about,
            email = user.email
    )
}

