package com.cwtsite.cwt.domain.schedule.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.schedule.service.ScheduleService
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleCreationDto
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleDto
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp

@RestController
@RequestMapping("api/schedule")
class ScheduleRestController {

    @Autowired private lateinit var scheduleService: ScheduleService
    @Autowired private lateinit var userService: UserService

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
}
