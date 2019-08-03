package com.cwtsite.cwt.domain.schedule.service

import com.cwtsite.cwt.domain.schedule.entity.Schedule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ScheduleService {

    @Autowired private lateinit var scheduleRepository: ScheduleRepository

    fun findAll(): MutableList<Schedule> = scheduleRepository.findAll()
}
