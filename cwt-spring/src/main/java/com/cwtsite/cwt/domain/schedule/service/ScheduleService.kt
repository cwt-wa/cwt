package com.cwtsite.cwt.domain.schedule.service

import com.cwtsite.cwt.core.news.PublishNews
import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*


@Service
class ScheduleService {

    @Autowired
    private lateinit var scheduleRepository: ScheduleRepository

    // called for when the schedule is created
    fun save(author: User, opponent: User, appointment: Timestamp): Schedule =
            scheduleRepository.save(Schedule(
                    author = author,
                    homeUser = author,
                    awayUser = opponent,
                    appointment = appointment))

    // called when the schedule is deleted by the user or the game has been reported
    fun delete(schedule: Schedule) =
            scheduleRepository.delete(schedule)

    fun findByPairing(user1: User, user2: User): Schedule? =
            scheduleRepository.findByPairing(user1, user2)

    fun findAll(): MutableList<Schedule> = scheduleRepository.findAll()

    fun findById(id: Long): Optional<Schedule> = scheduleRepository.findById(id)

    // called when a stream is scheduled for the schedule or that scheduled stream is cancelled
    fun save(schedule: Schedule): Schedule = scheduleRepository.save(schedule)
}
