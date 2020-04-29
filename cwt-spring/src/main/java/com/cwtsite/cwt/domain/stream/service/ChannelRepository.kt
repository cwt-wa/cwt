package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChannelRepository: JpaRepository<Channel, String> {

    fun findAllByUserIn(users: List<User>): List<Channel>
}
