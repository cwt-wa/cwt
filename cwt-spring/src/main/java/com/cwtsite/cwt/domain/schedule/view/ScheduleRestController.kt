package com.cwtsite.cwt.domain.schedule.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.schedule.service.ScheduleService
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleCreationDto
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleDto
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/schedule")
class ScheduleRestController {

    @Autowired private lateinit var scheduleService: ScheduleService
    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var authService: AuthService

    @RequestMapping("", method = [RequestMethod.GET])
    fun query(): ResponseEntity<List<ScheduleDto>> {
        return ResponseEntity.ok(scheduleService.findAll().map { ScheduleDto.toDto(it) })
    }

    @RequestMapping("", method = [RequestMethod.POST])
    fun save(@RequestBody dto: ScheduleCreationDto): ResponseEntity<ScheduleDto> {
        val author = userService.getById(dto.author).orElseThrow { RestException("No such author", HttpStatus.BAD_REQUEST, null) }
        val opponent = userService.getById(dto.opponent).orElseThrow { RestException("No such opponent", HttpStatus.BAD_REQUEST, null) }
        return ResponseEntity.ok(ScheduleDto.toDto(scheduleService.save(author, opponent, Timestamp.from(dto.appointment.toInstant()))))
    }

    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long, request: HttpServletRequest) {
        val authUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))

        val schedule = scheduleService.findById(id)
                .orElseThrow { RestException("No such schedule.", HttpStatus.NOT_FOUND, null) }

        if (schedule.homeUser != authUser && schedule.awayUser != authUser) {
            throw RestException("Forbidden.", HttpStatus.FORBIDDEN, null)
        }

        scheduleService.delete(schedule)
    }

    @RequestMapping("/{scheduleId}/channel/{channelId}", method = [RequestMethod.POST])
    fun scheduleStream(
            @PathVariable("scheduleId") scheduleId: Long,
            @PathVariable("channelId") channelId: String): ResponseEntity<Channel> {
        val schedule = scheduleService.findById(scheduleId)
                .orElseThrow { RestException("No such schedule.", HttpStatus.NOT_FOUND, null) }
        val channel = streamService.findChannel(channelId)
                .orElseThrow { RestException("No such channel.", HttpStatus.NOT_FOUND, null) }

        if (schedule.streams.contains(channel)) throw RestException(
                "Channel is already scheduled to stream this game", HttpStatus.BAD_REQUEST, null)

        schedule.streams.add(channel)
        scheduleService.save(schedule)

        return ResponseEntity.ok(channel)
    }

    @RequestMapping("/{scheduleId}/channel/{channelId}", method = [RequestMethod.DELETE])
    fun removeScheduledStream(
            @PathVariable("scheduleId") scheduleId: Long,
            @PathVariable("channelId") channelId: String) {
        val schedule = scheduleService.findById(scheduleId)
                .orElseThrow { RestException("No such schedule.", HttpStatus.NOT_FOUND, null) }
        val channel = streamService.findChannel(channelId)
                .orElseThrow { RestException("No such channel.", HttpStatus.NOT_FOUND, null) }

        schedule.streams.remove(channel)
        scheduleService.save(schedule)
    }
}
