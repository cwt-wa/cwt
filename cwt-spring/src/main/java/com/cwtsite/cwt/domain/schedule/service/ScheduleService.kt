package com.cwtsite.cwt.domain.schedule.service

import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp


@Service
class ScheduleService {

    @Autowired private lateinit var scheduleRepository: ScheduleRepository

    fun save(author: User, opponent: User, appointment: Timestamp): Schedule = scheduleRepository.save(Schedule(
            author = author,
            homeUser = author,
            awayUser = opponent,
            appointment = appointment
    ))

    fun findAll(): MutableList<Schedule> = scheduleRepository.findAll()
}
