package com.cwtsite.cwt.security.filter

import com.cwtsite.cwt.domain.user.service.JwtTokenUtil
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    private lateinit var securityContextHolderFacade: SecurityContextHolderFacade

    @Value("\${jwt.header}")
    private lateinit var tokenHeader: String

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if ("OPTIONS" == request.method) {
            chain.doFilter(request, response)
            return
        }

        val authToken: String? = request.getHeader(tokenHeader)

        if (authToken == null) {
            chain.doFilter(request, response)
            return
        }

        val username = jwtTokenUtil.getUsernameFromToken(authToken)

        logger.info(username)

        if (username != null && securityContextHolderFacade.authentication == null) {
            val userDetails: UserDetails
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username)
                if (!userDetails.isAccountNonLocked()) {
                    throw LockedException("${username} is locked")
                }
                logger.info("locked: ${!userDetails.isAccountNonLocked()}")
            } catch (e: UsernameNotFoundException) {
                logger.info(e.message)
                chain.doFilter(request, response)
                return
            } catch (e: LockedException) {
                logger.info(e.message)
                chain.doFilter(request, response)
                return
            }

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                securityContextHolderFacade.authentication = authentication
            }
        }

        chain.doFilter(request, response)
    }
}
