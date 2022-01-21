package com.cwtsite.cwt.domain.schedule.service

import com.cwtsite.cwt.core.news.PublishNews
import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Optional

@Service
class ScheduleService {

    @Autowired
    private lateinit var scheduleRepository: ScheduleRepository

    /**
     * When the schedule is cancelled by on of the partaking users.
     */
    @PublishNews
    @Transactional
    fun cancelSchedule(schedule: Schedule): Schedule {
        scheduleRepository.delete(schedule)
        return schedule
    }

    /**
     * When the schedule is removed not functionally by one of the partaking users.
     * For instance when the game has been reported, the schedule can be deleted.
     */
    @PublishNews
    @Transactional
    fun deleteSchedule(schedule: Schedule): Schedule {
        scheduleRepository.delete(schedule)
        return schedule
    }

    /**
     * When one of the partaking users has scheduled.
     */
    @PublishNews
    @Transactional
    fun createSchedule(author: User, opponent: User, appointment: Instant): Schedule {
        return scheduleRepository.save(
            Schedule(
                author = author,
                homeUser = author,
                awayUser = opponent,
                appointment = appointment
            )
        )
    }

    /**
     * Streamer has schedules a stream for the schedule.
     */
    @PublishNews
    @Transactional
    fun scheduleStream(schedule: Schedule, channel: Channel): Schedule {
        schedule.streams.add(channel)
        return scheduleRepository.save(schedule)
    }

    /**
     * Streamer has removed his scheduled stream from the scheduled game.
     */
    @PublishNews
    @Transactional
    fun removeStream(schedule: Schedule, channel: Channel): Schedule {
        schedule.streams.removeIf { it == channel }
        return scheduleRepository.save(schedule)
    }

    fun findByPairing(user1: User, user2: User): Schedule? =
        scheduleRepository.findByPairing(user1, user2)

    fun findAll(): MutableList<Schedule> = scheduleRepository.findAll()

    fun findById(id: Long): Optional<Schedule> = scheduleRepository.findById(id)
}
