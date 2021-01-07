package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChannelRepository: JpaRepository<Channel, String> {

    fun findAllByUserIn(users: List<User>): List<Channel>

    fun findByUser(user: User): Channel?

    fun findByLogin(login: String): Optional<Channel>

    @Modifying
    @Query("update Channel c set c.videoCursor = null")
    fun cleanPaginationCursors()
}
