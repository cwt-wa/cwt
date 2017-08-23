package com.cwtsite.cwt.tournament.view.controller;

import com.cwtsite.cwt.entity.Application;
import com.cwtsite.cwt.entity.Tournament;
import com.cwtsite.cwt.group.entity.Group;
import com.cwtsite.cwt.group.service.GroupService;
import com.cwtsite.cwt.group.view.model.GroupDto;
import com.cwtsite.cwt.tournament.service.TournamentService;
import com.cwtsite.cwt.tournament.view.model.StartNewTournamentDto;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.service.AuthService;
import com.cwtsite.cwt.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/tournament")
public class TournamentRestController {

    private final TournamentService tournamentService;
    private final AuthService authService;
    private final UserService userService;
    private GroupService groupService;

    @Autowired
    public TournamentRestController(TournamentService tournamentService, AuthService authService, UserService userService, GroupService groupService) {
        this.tournamentService = tournamentService;
        this.authService = authService;
        this.userService = userService;
        this.groupService = groupService;
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

    @RequestMapping(path = "/current/applications", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> getApplicantsOfCurrentTournament() {
        Tournament tournament = tournamentService.getCurrentTournament();
        return ResponseEntity.ok(tournamentService.getApplicants(tournament));
    }

    @RequestMapping(value = "{id}/group/many", method = RequestMethod.POST)
    public ResponseEntity<?> addManyGroups(@PathVariable("id") long id, @RequestBody List<GroupDto> groupDtos) {
        Tournament tournament = tournamentService.getTournament(id);

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
                    return GroupDto.map(tournament, groupMembers, groupDto.getLabel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(groupService.save(groups));
    }

    @RequestMapping(value = "current/group/many", method = RequestMethod.POST)
    public ResponseEntity<?> addManyGroups(@RequestBody List<GroupDto> groupDtos) {
        // TODO Change tournament status
        Tournament tournament = tournamentService.getCurrentTournament();
        return addManyGroups(tournament.getId(), groupDtos);
    }

    @RequestMapping(value = "{id}/group", method = RequestMethod.GET)
    public ResponseEntity<List<?>> getGroupsForTournament(@PathVariable("id") long id) {
        Tournament tournament = tournamentService.getTournament(id);
        return ResponseEntity.ok(groupService.getGroupsForTournament(tournament));
    }

    @RequestMapping(value = "current/group", method = RequestMethod.GET)
    public ResponseEntity<List<?>> getGroupsForCurrentTournament() {
        Tournament currentTournament = tournamentService.getCurrentTournament();
        return getGroupsForTournament(currentTournament.getId());
    }
}
