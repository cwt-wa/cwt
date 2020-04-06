package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class UserRegistrationDto (
        val username: String,
        val email: String,
        val password: String,
        val captchaToken: String,
        val wormnetChannel: String
)
