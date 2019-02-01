package com.cwtsite.cwt.domain.tournament.view.controller;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.service.GroupService;
import com.cwtsite.cwt.domain.group.view.model.GroupDto;
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.tournament.view.model.StartNewTournamentDto;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.AuthService;
import com.cwtsite.cwt.domain.user.service.UserService;
import com.cwtsite.cwt.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/tournament")
public class TournamentRestController {

    private final TournamentService tournamentService;
    private final AuthService authService;
    private final UserService userService;
    private final GroupService groupService;
    private final PlayoffService playoffService;

    @Autowired
    public TournamentRestController(TournamentService tournamentService, AuthService authService, UserService userService,
                                    GroupService groupService, PlayoffService playoffService) {
        this.tournamentService = tournamentService;
        this.authService = authService;
        this.userService = userService;
        this.groupService = groupService;
        this.playoffService = playoffService;
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public Tournament createTournament(HttpServletRequest request, @RequestBody StartNewTournamentDto startNewTournamentDto) {
        return tournamentService.startNewTournament(startNewTournamentDto.getModeratorIds());
    }

    @RequestMapping(path = "/{idOrYear}", method = RequestMethod.GET)
    public ResponseEntity<Tournament> getTournament(@PathVariable("idOrYear") long idOrYear) {
        final Optional<Tournament> tournament = String.valueOf(idOrYear).startsWith("20")
                ? tournamentService.getTournamentByYear(idOrYear)
                : tournamentService.getTournament(idOrYear);

        return tournament
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public ResponseEntity<Tournament> getCurrentTournament() {
        Tournament tournament = tournamentService.getCurrentTournament();

        return tournament == null
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(tournament);
    }

    @RequestMapping(path = "/current/applications", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> getApplicantsOfCurrentTournament() {
        Tournament tournament = tournamentService.getCurrentTournament();
        return ResponseEntity.ok(tournamentService.getApplicants(tournament));
    }

    @RequestMapping(value = "{id}/group/many", method = RequestMethod.POST)
    public ResponseEntity<?> addManyGroups(@PathVariable("id") long id, @RequestBody List<GroupDto> groupDtos) {
        Optional<Tournament> tournament = tournamentService.getTournament(id);

        if (!tournament.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Long> userIds = groupDtos.stream()
                .map(GroupDto::getUsers)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<User> users = userService.getByIds(userIds);

        List<Group> groups = groupDtos.stream()
                .map(groupDto -> {
                    List<User> groupMembers = users.stream()
                            .filter(user -> groupDto.getUsers().stream()
                                    .anyMatch(userId -> Objects.equals(userId, user.getId())))
                            .collect(Collectors.toList());
                    return GroupDto.map(tournament.get(), groupMembers, groupDto.getLabel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(groupService.startGroupStage(tournament.get(), groups));
    }

    @RequestMapping(value = "current/group/many", method = RequestMethod.POST)
    public ResponseEntity<?> addManyGroups(@RequestBody List<GroupDto> groupDtos) {
        // TODO Change tournament status
        Tournament tournament = tournamentService.getCurrentTournament();
        return addManyGroups(tournament.getId(), groupDtos);
    }

    @RequestMapping(value = "{id}/group", method = RequestMethod.GET)
    public ResponseEntity<List<Group>> getGroupsForTournament(@PathVariable("id") long id) {
        return tournamentService.getTournament(id)
                .map(t -> ResponseEntity.ok(groupService.getGroupsForTournament(t)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @RequestMapping(value = "current/group", method = RequestMethod.GET)
    public ResponseEntity<List<Group>> getGroupsForCurrentTournament() {
        Tournament currentTournament = tournamentService.getCurrentTournament();
        return getGroupsForTournament(currentTournament.getId());
    }

    @RequestMapping(value = "{id}/game/playoff", method = RequestMethod.GET)
    public ResponseEntity<List<Game>> getPlayoffGames(@PathVariable("id") long id) {
        return tournamentService.getTournament(id)
                .map(t -> ResponseEntity.ok(playoffService.getGamesOfTournament(t)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @RequestMapping(value = "current/game/playoff", method = RequestMethod.GET)
    public ResponseEntity<List<Game>> getPlayoffGamesOfCurrentTournament() {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        return getPlayoffGames(currentTournament.getId());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAll());
    }

    @RequestMapping(value = "current/playoffs/start", method = RequestMethod.POST)
    public ResponseEntity<List<Game>> startPlayoffs(@RequestBody final List<GameCreationDto> gameCreationDtos) {
        final List<Long> userIds = gameCreationDtos.stream()
                .map(gameCreationDto -> Arrays.asList(new Long[]{gameCreationDto.getHomeUser(), gameCreationDto.getAwayUser()}))
                .reduce((longs, longs2) -> {
                    final ArrayList<Long> concatenatedLongs = new ArrayList<>(longs);
                    concatenatedLongs.addAll(longs2);
                    return concatenatedLongs;
                })
                .orElseGet(Collections::emptyList);

        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final List<User> users = userService.getByIds(userIds);

        final List<Game> games = gameCreationDtos.stream()
                .map(dto -> GameCreationDto.fromDto(
                        dto,
                        users.stream()
                                .filter(u -> Objects.equals(u.getId(), dto.getHomeUser()))
                                .findFirst().orElseThrow(RuntimeException::new),
                        users.stream()
                                .filter(u -> Objects.equals(u.getId(), dto.getAwayUser()))
                                .findFirst().orElseThrow(RuntimeException::new),
                        currentTournament
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(tournamentService.startPlayoffs(games));
    }
}
