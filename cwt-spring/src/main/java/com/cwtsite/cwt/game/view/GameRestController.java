package com.cwtsite.cwt.game.view;

import com.cwtsite.cwt.core.exception.ResourceNotFoundException;
import com.cwtsite.cwt.entity.Comment;
import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.game.entity.Rating;
import com.cwtsite.cwt.game.service.GameService;
import com.cwtsite.cwt.game.view.model.GameDto;
import com.cwtsite.cwt.game.view.model.ReportDto;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.tournament.service.TournamentService;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/game")
public class GameRestController {

    private final UserService userService;
    private final GameService gameService;
    private final TournamentService tournamentService;

    @Autowired
    public GameRestController(GameService gameService, UserService userService, TournamentService tournamentService) {
        this.gameService = gameService;
        this.userService = userService;
        this.tournamentService = tournamentService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Game> reportGame(@PathVariable("id") long id) {
        return ResponseEntity.ok(gameService.get(id));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Game> reportGame(@RequestBody final ReportDto reportDto) {
        return ResponseEntity.ok(gameService.reportGame(reportDto));
    }

    @RequestMapping(value = "/many", method = RequestMethod.POST)
    public ResponseEntity<List<Game>> reportGame(@RequestBody final List<GameDto> gameDtos) {
        final List<Long> userIds = gameDtos.stream()
                .map(gameDto -> Arrays.asList(new Long[]{gameDto.getHomeUser(), gameDto.getAwayUser()}))
                .reduce((longs, longs2) -> {
                    final ArrayList<Long> concatenatedLongs = new ArrayList<>(longs);
                    concatenatedLongs.addAll(longs2);
                    return concatenatedLongs;
                })
                .orElseGet(Collections::emptyList);

        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final List<User> users = userService.getByIds(userIds);

        final List<Game> games = gameDtos.stream()
                .map(dto -> GameDto.map(
                        dto,
                        users.stream()
                                .filter(u -> Objects.equals(u.getId(), dto.getHomeUser()))
                                .findFirst().orElseThrow(ResourceNotFoundException::new),
                        users.stream()
                                .filter(u -> Objects.equals(u.getId(), dto.getAwayUser()))
                                .findFirst().orElseThrow(ResourceNotFoundException::new),
                        currentTournament
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(gameService.saveAll(games));
    }

    @RequestMapping(value = "/{id}/rating", method = RequestMethod.POST)
    public Rating rateGame(@PathVariable("id") long id, @RequestBody RatingDto rating) {
        return gameService.rateGame(id, rating.getUser(), rating.getType());
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.POST)
    public Comment commentGame(@PathVariable("id") long id, @RequestBody CommentDto comment) {
        return gameService.commentGame(id, comment.getUser(), comment.getBody());
    }
}
