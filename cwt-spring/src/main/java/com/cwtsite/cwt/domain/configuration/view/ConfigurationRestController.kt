package com.cwtsite.cwt.domain.configuration.view

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.configuration.view.model.ConfigurationDto
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/configuration")
class ConfigurationRestController @Autowired
constructor(private val configurationService: ConfigurationService, private val tournamentService: TournamentService,
            private val authService: AuthService, private val gameService: GameService) {

    @RequestMapping("", method = [RequestMethod.GET])
    fun query(@RequestParam(value = "keys", required = false) configurationKeys: List<ConfigurationKey>?): List<Configuration> {
        return if (configurationKeys == null) configurationService.findAll() else configurationService.findAll(configurationKeys)
    }

    @RequestMapping("", method = [RequestMethod.POST])
    fun query(@RequestBody configurationDto: ConfigurationDto, request: HttpServletRequest): ResponseEntity<Configuration> {
        val authenticatedUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        val configuration = ConfigurationDto.map(configurationDto, authenticatedUser)
        return ResponseEntity.ok(configurationService.save(configuration))
    }

    @RequestMapping("/score-best-of", method = [RequestMethod.GET])
    fun fetchBestOfScore(): ResponseEntity<Configuration> {
        return ResponseEntity.ok(gameService.getBestOfValue(tournamentService.getCurrentTournament().status))
    }
}
