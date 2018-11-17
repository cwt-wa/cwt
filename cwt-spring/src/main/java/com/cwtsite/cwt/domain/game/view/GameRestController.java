package com.cwtsite.cwt.domain.game.view;

import com.cwtsite.cwt.core.FileValidator;
import com.cwtsite.cwt.domain.core.exception.BadRequestException;
import com.cwtsite.cwt.domain.core.exception.NotFoundException;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.Rating;
import com.cwtsite.cwt.domain.game.service.GameService;
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto;
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto;
import com.cwtsite.cwt.domain.game.view.model.ReportDto;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.UserService;
import com.cwtsite.cwt.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<GameDetailDto> getGame(@PathVariable("id") long id) {
        return gameService.get(id)
                .map(body -> ResponseEntity.ok(GameDetailDto.toDto(body)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<GameCreationDto> reportGameWithoutReplay(@RequestBody final ReportDto reportDto) {
        final Game reportedGame = gameService.reportGame(
                reportDto.getUser(), reportDto.getOpponent(),
                reportDto.getScoreOfUser().intValue(), reportDto.getScoreOfOpponent().intValue());
        return ResponseEntity.ok(GameCreationDto.toDto(reportedGame));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<GameCreationDto> reportGameWithReplayFile(
            @RequestParam("replay") MultipartFile replay,
            @RequestParam("score-home") int scoreHome,
            @RequestParam("score-away") int scoreAway,
            @RequestParam("home-user") long homeUser,
            @RequestParam("away-user") long awayUser) throws IOException {
        final Game game;
        try {
            game = gameService.reportGame(homeUser, awayUser, scoreHome, scoreAway, replay);
        } catch (GameService.InvalidOpponentException
                | GameService.InvalidScoreException
                | FileValidator.UploadSecurityException
                | FileValidator.FileEmptyException
                | FileValidator.IllegalFileContentTypeException
                | FileValidator.FileTooLargeException
                | FileValidator.IllegalFileExtension e) {
            throw new BadRequestException(e.getMessage());
        }
        return ResponseEntity.ok(GameCreationDto.toDto(game));
    }

    @RequestMapping(value = "/{gameId}/replay", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable("gameId") long gameId) throws IOException {
        final Game game = gameService.get(gameId).orElseThrow(NotFoundException::new);

        if (game.getReplay() == null) {
            throw new NotFoundException("There's no replay file for this game.");
        }

        ByteArrayResource resource = new ByteArrayResource(game.getReplay().getFile());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + gameService.createReplayFileName(game))
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(game.getReplay().getMediaType()))
                .body(resource);
    }

    @RequestMapping(value = "/many", method = RequestMethod.POST)
    public ResponseEntity<List<Game>> reportManyGamesWithoutReportFile(@RequestBody final List<GameCreationDto> gameCreationDtos) {
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
                                .findFirst().orElseThrow(NotFoundException::new),
                        users.stream()
                                .filter(u -> Objects.equals(u.getId(), dto.getAwayUser()))
                                .findFirst().orElseThrow(NotFoundException::new),
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
