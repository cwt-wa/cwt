package com.cwtsite.cwt.domain.schedule.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import java.time.Instant

@DataTransferObject
data class ScheduleCreationDto(
    val author: Long,
    val opponent: Long,
    val appointment: Instant
)
