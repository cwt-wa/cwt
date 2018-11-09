package com.cwtsite.cwt.domain.configuration.view;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.configuration.view.model.ConfigurationDto;
import com.cwtsite.cwt.domain.game.service.GameService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "api/configuration")
public class ConfigurationRestController {


    private final ConfigurationService configurationService;
    private final TournamentService tournamentService;
    private final AuthService authService;
    private final GameService gameService;

    @Autowired
    public ConfigurationRestController(ConfigurationService configurationService, TournamentService tournamentService,
                                       AuthService authService, GameService gameService) {
        this.configurationService = configurationService;
        this.tournamentService = tournamentService;
        this.authService = authService;
        this.gameService = gameService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Configuration> query(@RequestParam(value = "keys", required = false) List<ConfigurationKey> configurationKeys) {
        if (configurationKeys == null) {
            return configurationService.getAll();
        }

        return configurationService.getAll(configurationKeys);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Configuration> query(@RequestBody ConfigurationDto configurationDto, HttpServletRequest request) {
        final User authenticatedUser = authService.getUserFromToken(request.getHeader(authService.getTokenHeaderName()));
        final Configuration configuration = ConfigurationDto.map(configurationDto, authenticatedUser);
        return ResponseEntity.ok(configurationService.save(configuration));
    }

    @RequestMapping(value = "/score-best-of", method = RequestMethod.GET)
    public ResponseEntity<Configuration> getBestOfScore() {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        return ResponseEntity.ok(gameService.getBestOfValue(currentTournament.getStatus()));
    }
}
