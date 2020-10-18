package com.cwtsite.cwt.domain.user.service

import com.cwtsite.cwt.domain.user.view.model.JwtUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val expiration: Long? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getUsernameFromToken(token: String): String? {
        return try {
            val claims = getClaimsFromToken(token)
            claims!!.subject
        } catch (e: Exception) {
            null
        }
    }

    fun getCreatedDateFromToken(token: String): Date? {
        return try {
            val claims = getClaimsFromToken(token)
            Date(claims!![CLAIM_KEY_CREATED] as Long)
        } catch (e: Exception) {
            null
        }
    }

    fun getExpirationDateFromToken(token: String): Date? {
        return try {
            val claims = getClaimsFromToken(token)
            claims!!.expiration
        } catch (e: Exception) {
            null
        }
    }

    fun getAudienceFromToken(token: String): String? {
        return try {
            val claims = getClaimsFromToken(token)
            claims!![CLAIM_KEY_AUDIENCE] as String
        } catch (e: Exception) {
            null
        }
    }

    private fun getClaimsFromToken(token: String): Claims? {
        return try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .body
        } catch (e: Exception) {
            null
        }
    }

    private fun generateExpirationDate(): Date {
        return Date(System.currentTimeMillis() + expiration!! * 1000)
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration!!.before(Date())
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date?, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created!!.before(lastPasswordReset)
    }

    private fun ignoreTokenExpiration(token: String): Boolean {
        val audience = getAudienceFromToken(token)
        return AUDIENCE_TABLET == audience || AUDIENCE_MOBILE == audience
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        claims[CLAIM_KEY_USERNAME] = userDetails.username
        claims[CLAIM_KEY_CREATED] = Date()
        claims[PUBLIC_CLAIM_KEY_CONTEXT] = JwtTokenContext(userDetails as JwtUser<*>)
        return generateToken(claims)
    }

    private fun generateToken(claims: Map<String, Any>): String {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date?): Boolean? {
        val created = getCreatedDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && (!isTokenExpired(token) || ignoreTokenExpiration(token))
    }

    fun refreshToken(token: String): String? {
        var refreshedToken: String?
        try {
            val claims = getClaimsFromToken(token)
            claims!![CLAIM_KEY_CREATED] = Date()
            refreshedToken = generateToken(claims)
        } catch (e: Exception) {
            logger.warn("Token refresh failed", e)
            refreshedToken = null
        }

        return refreshedToken
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val user = userDetails as JwtUser<*>
        val username = getUsernameFromToken(token)
        val created = getCreatedDateFromToken(token)
        return (username == user.username
                && !isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.resetDate))
    }

    companion object {

        private const val CLAIM_KEY_USERNAME = "sub"
        private const val CLAIM_KEY_AUDIENCE = "audience"
        private const val CLAIM_KEY_CREATED = "created"
        private const val PUBLIC_CLAIM_KEY_CONTEXT = "context"

        private const val AUDIENCE_MOBILE = "mobile"
        private const val AUDIENCE_TABLET = "tablet"
    }
}
