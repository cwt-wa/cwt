package com.cwtsite.cwt.domain.schedule.service

import com.cwtsite.cwt.domain.schedule.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ScheduleRepository : JpaRepository<Schedule, Long>
