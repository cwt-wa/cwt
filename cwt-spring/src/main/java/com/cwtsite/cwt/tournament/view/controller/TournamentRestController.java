package com.cwtsite.cwt.tournament.view.controller;

import com.cwtsite.cwt.entity.Tournament;
import com.cwtsite.cwt.tournament.service.TournamentService;
import com.cwtsite.cwt.tournament.view.model.StartNewTournamentDto;
import com.cwtsite.cwt.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "api/tournament")
public class TournamentRestController {

    private final TournamentService tournamentService;
    private final AuthService authService;

    @Autowired
    public TournamentRestController(TournamentService tournamentService, AuthService authService) {
        this.tournamentService = tournamentService;
        this.authService = authService;
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public Tournament createTournament(HttpServletRequest request, @RequestBody StartNewTournamentDto startNewTournamentDto) {
        return tournamentService.startNewTournament(
                authService.getUserFromToken(request.getHeader(authService.getTokenHeaderName())),
                startNewTournamentDto.getModeratorIds());
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Tournament> getTournament(@PathVariable("id") long id) {
        Tournament tournament = tournamentService.getTournament(id);

        return tournament == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                : ResponseEntity.ok(tournament);
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public ResponseEntity<Tournament> getCurrentTournament() {
        Tournament tournament = tournamentService.getCurrentTournament();

        return tournament == null
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)
                : ResponseEntity.ok(tournament);
    }
}
