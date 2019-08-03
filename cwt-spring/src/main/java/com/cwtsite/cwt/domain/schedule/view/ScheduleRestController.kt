package com.cwtsite.cwt.domain.schedule.view

import com.cwtsite.cwt.domain.schedule.service.ScheduleService
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/schedule")
class ScheduleRestController {

    @Autowired private lateinit var scheduleService: ScheduleService

    @RequestMapping("", method = [RequestMethod.GET])
    fun query(): ResponseEntity<List<ScheduleDto>> {
        return ResponseEntity.ok(scheduleService.findAll().map { ScheduleDto.toDto(it) })
    }
}
