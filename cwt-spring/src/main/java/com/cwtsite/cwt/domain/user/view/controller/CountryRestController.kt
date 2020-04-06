package com.cwtsite.cwt.domain.user.view.controller

import com.cwtsite.cwt.domain.user.repository.CountryRepository
import com.cwtsite.cwt.domain.user.view.model.CountryDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/country")
class CountryRestController {

    @Autowired private lateinit var countryRepository: CountryRepository

    @RequestMapping(method = [RequestMethod.GET])
    fun query(): ResponseEntity<List<CountryDto>> =
            ResponseEntity.ok(countryRepository.findAll().map { CountryDto.toDto(it) })
}
