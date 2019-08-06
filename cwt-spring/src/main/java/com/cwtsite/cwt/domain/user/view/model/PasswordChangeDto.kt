package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class PasswordChangeDto(
        val currentPassword: String,
        val newPassword: String)
