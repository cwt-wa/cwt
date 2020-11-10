package com.cwtsite.cwt.domain.user.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.JwtTokenUtil
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationRequest
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationResponse
import com.cwtsite.cwt.domain.user.view.model.JwtUser
import com.cwtsite.cwt.domain.user.view.model.UserRegistrationDto
import com.cwtsite.cwt.security.FirebaseIdentityTokenDto
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import com.cwtsite.cwt.security.SecurityService
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.File
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("api/auth")
class AuthenticationRestController @Autowired
constructor(private val authenticationManager: AuthenticationManager, private val jwtTokenUtil: JwtTokenUtil,
            private val userDetailsService: UserDetailsService, private val userService: UserService,
            private val securityContextHolderFacade: SecurityContextHolderFacade,
            private val authService: AuthService, private val securityService: SecurityService) {

    @Value("\${firebase-credentials-location}")
    private lateinit var firebaseCredentialsLocation: String

    private val logger = LoggerFactory.getLogger(this::class.java)


    @RequestMapping(path = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestBody userRegistrationDto: UserRegistrationDto): ResponseEntity<*> {
        if (!securityService.verifySecretWord(userRegistrationDto.wormnetChannel)) {
            throw RestException("Registration is forbidden for you.", HttpStatus.FORBIDDEN, null)
        }

        if (!securityService.verifyToken(userRegistrationDto.captchaToken)) {
            throw RestException("Registration is forbidden for you.", HttpStatus.FORBIDDEN, null)
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
            @RequestBody authenticationRequest: JwtAuthenticationRequest): ResponseEntity<JwtAuthenticationResponse> {
        val authentication = try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            authenticationRequest.username,
                            authenticationRequest.password
                    )
            )
        } catch (e: UsernameNotFoundException) {
            throw RestException("Wrong credentials.", HttpStatus.BAD_REQUEST, null)
        } catch (e: BadCredentialsException) {
            throw RestException("Wrong credentials.", HttpStatus.BAD_REQUEST, null)
        }

        securityContextHolderFacade.authentication = authentication

        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val token = jwtTokenUtil.generateToken(userDetails)

        return ResponseEntity.ok(JwtAuthenticationResponse(token))
    }

    @RequestMapping("/firebase-login", method = [RequestMethod.POST])
    @Throws(AuthenticationException::class)
    fun createAuthenticationTokenForFirebase(
            @RequestBody authRequest: JwtAuthenticationRequest): ResponseEntity<FirebaseIdentityTokenDto> {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
                authRequest.username, authRequest.password))

        val firebaseApp = when (FirebaseApp.getApps().isEmpty()) {
            false -> FirebaseApp.getInstance()
            true -> File(firebaseCredentialsLocation).inputStream().use {
                FirebaseApp.initializeApp(FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(it))
                        .build())
            }
        }

        val userDetails = userDetailsService.loadUserByUsername(authRequest.username) as JwtUser<*>

        val additionalClaims = mapOf<String, Any?>(
                "username" to userDetails.username,
                "id" to userDetails.id,
                "email" to userDetails.email,
                "isAdmin" to userDetails.roles.contains(AuthorityRole.ROLE_ADMIN))
        val customToken = FirebaseAuth.getInstance(firebaseApp!!).createCustomToken(
                "${authRequest.username}_${userDetails.id}", additionalClaims)

        return ResponseEntity.ok(securityService.exchangeFirebaseCustomTokenForIdToken(customToken))
    }

    @RequestMapping("/firebase-refresh", method = [RequestMethod.POST])
    fun refreshAndGetAuthenticationTokenForFirebase(
            @RequestBody jwtAuthenticationResponse: JwtAuthenticationResponse): ResponseEntity<FirebaseIdentityTokenDto> {
        try {
            return ResponseEntity.ok(securityService.refreshFirebaseToken(jwtAuthenticationResponse.token))
        } catch (e: Exception) {
            throw RestException(e.message ?: "Undetermined authorization error.", HttpStatus.UNAUTHORIZED, e)
        }
    }

    @RequestMapping("/refresh", method = [RequestMethod.GET])
    fun refreshAndGetAuthenticationToken(request: HttpServletRequest): ResponseEntity<JwtAuthenticationResponse> {
        val token = request.getHeader(authService.tokenHeaderName) ?: return ResponseEntity.ok(null)

        val username = jwtTokenUtil.getUsernameFromToken(token)
                ?: throw RestException("Username is null in token.", HttpStatus.BAD_REQUEST, null)
        val user = userDetailsService.loadUserByUsername(username) as JwtUser<*>

        return if (jwtTokenUtil.canTokenBeRefreshed(token, user.resetDate)!!) {
            val refreshedToken = jwtTokenUtil.refreshToken(token) ?: return ResponseEntity.ok(null)
            ResponseEntity.ok(JwtAuthenticationResponse(refreshedToken))
        } else {
            logger.warn("Token could not be refreshed.")
            return ResponseEntity.ok(null)
        }
    }
}
