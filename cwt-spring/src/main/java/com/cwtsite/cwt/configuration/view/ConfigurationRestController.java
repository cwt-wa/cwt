package com.cwtsite.cwt.configuration.view;

import com.cwtsite.cwt.configuration.entity.Configuration;
import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.configuration.service.ConfigurationService;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.tournament.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/configuration")
public class ConfigurationRestController {


    private final ConfigurationService configurationService;
    private final TournamentService tournamentService;

    @Autowired
    public ConfigurationRestController(ConfigurationService configurationService, TournamentService tournamentService) {
        this.configurationService = configurationService;
        this.tournamentService = tournamentService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Configuration> query(@RequestParam(value = "keys", required = false) List<ConfigurationKey> configurationKeys) {
        if (configurationKeys == null) {
            return configurationService.getAll();
        }

        return configurationService.getAll(configurationKeys);
    }

    @RequestMapping(value = "/score-best-of", method = RequestMethod.GET)
    public ResponseEntity<?> getBestOfScore() {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        ConfigurationKey configurationKey;

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            configurationKey = ConfigurationKey.GROUP_GAMES_BEST_OF;
        } else if (currentTournament.getStatus() == TournamentStatus.PLAYOFFS) {
            configurationKey = ConfigurationKey.PLAYOFF_GAMES_BEST_OF;
        } else {
            return ResponseEntity.badRequest().body("Tournament is neither in group stage nor playoffs. Thus, there's no definition on what game scores can be reported.");
        }

        return ResponseEntity.ok(configurationService.getOne(configurationKey));
    }
}
