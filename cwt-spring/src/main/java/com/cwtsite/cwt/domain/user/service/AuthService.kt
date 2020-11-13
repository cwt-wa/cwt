package com.cwtsite.cwt.domain.user.service

import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component

import java.security.MessageDigest

@Component
class AuthService {

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    private lateinit var userRepository: UserRepository

    @Value("\${jwt.header}")
    val tokenHeaderName: String? = null

    @Value("\${password.salt}")
    private val salt: String? = null

    @Value("\${password.bcrypt-salt}")
    private val bCryptSalt: String? = null


    fun getUserFromToken(token: String): User? {
        val usernameFromToken = jwtTokenUtil.getUsernameFromToken(token) ?: return null
        return userRepository.findByUsername(usernameFromToken)
    }

    fun createHash(plainPassword: String?): String {
        return if (plainPassword == null) "" else BCrypt.hashpw(plainPassword, bCryptSalt!!)
    }

    fun createLegacyHash(plainPassword: String?): String {
        var saltedPassword = salt!! + plainPassword!!
        val chars = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        try {
            val engine = MessageDigest.getInstance("SHA-1")
            val result = engine.digest(saltedPassword.toByteArray(charset("UTF-8")))
            val buffer = StringBuilder(result.size * 2)
            for (aData in result) {
                val value1 = aData.toInt() and 0xFF
                buffer.append(chars[value1 / 16])
                buffer.append(chars[value1 and 0x0F])
            }
            return buffer.toString().toLowerCase()
        } catch (e: Exception) {
            throw RuntimeException("No hash implementation.", e)
        }

    }
}
