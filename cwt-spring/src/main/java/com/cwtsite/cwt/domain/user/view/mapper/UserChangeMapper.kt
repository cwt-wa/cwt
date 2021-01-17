package com.cwtsite.cwt.domain.user.view.mapper

import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.view.model.UserChangeDto
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserChangeMapper {

    fun toDto(user: User) = UserChangeDto(
        username = user.username,
        country = user.country.id,
        about = user.about,
        email = user.email
    )
}
