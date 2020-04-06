package com.cwtsite.cwt.security

data class FirebaseIdentityTokenDto (
        var kind: String? = null,
        val idToken: String,
        val refreshToken: String,
        var expiresIn: String? = null,
        var isNewUser: Boolean? = null
)
