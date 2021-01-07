package com.cwtsite.cwt.domain.user.repository

import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun findByUsernameIgnoreCase(username: String): User?

    fun findAllByUsernameIgnoreCase(username: String): List<User>

    fun findAllByEmailIgnoreCase(email: String): List<User>

    fun findByEmailEqualsOrUsernameEquals(email: String, username: String): Optional<User>

    fun findAllByUsernameContaining(username: String): List<User>

    fun findAllByUsernameIn(usernames: List<String>): List<User>

    fun findByResetKey(resetKey: String): User?

    @Query("select lower(u.username) from User u")
    fun findAllUsernamesToLowerCase(): List<String>

    @Query("select u from User u where lower(u.username) in (:usernames)")
    fun findAllByUsernameLowercaseIn(@Param("usernames") username: List<String>): List<User>

    @Modifying
    @Query("REFRESH MATERIALIZED VIEW CONCURRENTLY user_stats", nativeQuery = true)
    fun refreshUserStats()
}
