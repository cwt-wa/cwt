package com.cwtsite.cwt.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/** If that name doesn't speak Java, then I don't know. */
@Component
class SecurityContextHolderFacade {

    val authenticationName: String?
        get() = SecurityContextHolder.getContext().authentication?.name
}
