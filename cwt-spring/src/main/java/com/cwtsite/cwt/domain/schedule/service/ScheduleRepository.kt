package com.cwtsite.cwt.domain.schedule.service

import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository : JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s where (s.homeUser = :user1 and s.awayUser = :user2) or (s.homeUser = :user2 and s.awayUser = :user1)")
    fun findByPairing(@Param("user1") user1: User, @Param("user2") user2: User): Schedule?
}
