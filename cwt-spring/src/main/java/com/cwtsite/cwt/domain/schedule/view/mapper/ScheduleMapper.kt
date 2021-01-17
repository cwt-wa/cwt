package com.cwtsite.cwt.domain.schedule.view.mapper

import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleDto
import com.cwtsite.cwt.domain.stream.view.mapper.ChannelMapper
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ScheduleMapper {

    @Autowired private lateinit var channelMapper: ChannelMapper

    fun toDto(schedule: Schedule) = ScheduleDto(
        id = schedule.id!!,
        homeUser = UserMinimalDto(id = schedule.homeUser.id!!, username = schedule.homeUser.username),
        awayUser = UserMinimalDto(id = schedule.awayUser.id!!, username = schedule.awayUser.username),
        appointment = schedule.appointment,
        author = UserMinimalDto(id = schedule.author.id!!, username = schedule.author.username),
        streams = schedule.streams.map { channelMapper.toDto(it) },
        created = schedule.created!!
    )
}
