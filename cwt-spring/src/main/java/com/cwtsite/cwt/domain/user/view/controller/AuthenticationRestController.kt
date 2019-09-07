package com.cwtsite.cwt.domain.user.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.JwtTokenUtil
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationRequest
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationResponse
import com.cwtsite.cwt.domain.user.view.model.JwtUser
import com.cwtsite.cwt.domain.user.view.model.UserRegistrationDto
import com.cwtsite.cwt.security.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/auth")
class AuthenticationRestController @Autowired
constructor(private val authenticationManager: AuthenticationManager, private val jwtTokenUtil: JwtTokenUtil,
            private val userDetailsService: UserDetailsService, private val userService: UserService,
            private val authService: AuthService, private val securityService: SecurityService) {

    @RequestMapping(path = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestBody userRegistrationDto: UserRegistrationDto): ResponseEntity<*> {
        if (!securityService.verifySecretWord(userRegistrationDto.wormnetChannel)) {
            throw RestException("Registration is forbidden for you.", HttpStatus.FORBIDDEN, null);
        }

        if (!securityService.verifyToken(userRegistrationDto.captchaToken)) {
            throw RestException("Registration is forbidden for you.", HttpStatus.FORBIDDEN, null);
        }

        val user: User
        try {
            user = userService.registerUser(
                    userRegistrationDto.username, userRegistrationDto.email, userRegistrationDto.password)
        } catch (e: UserService.UserExistsByEmailOrUsernameException) {
            throw RestException("User already exists.", HttpStatus.BAD_REQUEST, e)
        } catch (e: UserService.InvalidUsernameException) {
            throw RestException("Username invalid.", HttpStatus.BAD_REQUEST, e)
        } catch (e: UserService.InvalidEmailException) {
            throw RestException("Email invalid.", HttpStatus.BAD_REQUEST, e)
        }

        return createAuthenticationToken(
                JwtAuthenticationRequest(user.username, userRegistrationDto.password))
    }

    @RequestMapping("/login", method = [RequestMethod.POST])
    @Throws(AuthenticationException::class)
    fun createAuthenticationToken(
            @RequestBody authenticationRequest: JwtAuthenticationRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        authenticationRequest.username,
                        authenticationRequest.password
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val token = jwtTokenUtil.generateToken(userDetails)

        return ResponseEntity.ok(JwtAuthenticationResponse(token))
    }

    @RequestMapping("/refresh", method = [RequestMethod.GET])
    fun refreshAndGetAuthenticationToken(request: HttpServletRequest): ResponseEntity<JwtAuthenticationResponse> {
        val token = request.getHeader(authService.tokenHeaderName) ?: return ResponseEntity.ok(null)

        val username = jwtTokenUtil.getUsernameFromToken(token)
        val user = userDetailsService.loadUserByUsername(username) as JwtUser<*>

        return if (jwtTokenUtil.canTokenBeRefreshed(token, user.resetDate)!!) {
            val refreshedToken = jwtTokenUtil.refreshToken(token)
            ResponseEntity.ok(JwtAuthenticationResponse(refreshedToken))
        } else {
            ResponseEntity.badRequest().body(null)
        }
    }
}

