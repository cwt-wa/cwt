package com.cwtsite.cwt.domain.user.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class JwtAuthenticationResponse(val token: String)
