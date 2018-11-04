package com.cwtsite.cwt.domain.playoffs.service;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.PlayoffGame;
import com.cwtsite.cwt.domain.game.service.GameRepository;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PlayoffService {

    private final GameRepository gameRepository;
    private final TournamentService tournamentService;

    @Autowired
    public PlayoffService(GameRepository gameRepository, TournamentService tournamentService) {
        this.gameRepository = gameRepository;
        this.tournamentService = tournamentService;
    }

    public List<Game> getGamesOfTournament(final Tournament tournament) {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournament);
    }

    public Game getNextGameForUser(final User user) {
        return gameRepository.findNextPlayoffGameForUser(tournamentService.getCurrentTournament(), user);
    }

    /**
     * @deprecated in favor of {@link #isFinalGame(Game)}
     */
    @Deprecated
    public boolean finalGamesAreNext() {
        final Tournament currentTournament = tournamentService.getCurrentTournament();

        if (currentTournament.getStatus() != TournamentStatus.PLAYOFFS) {
            return false;
        }

        final List<Game> playoffGames = gameRepository.findByTournamentAndPlayoffIsNotNull(currentTournament);

        final int numberOfGamesInFirstRound = playoffGames.stream()
                .filter(game -> game.getPlayoff().getRound() == 1)
                .collect(Collectors.toList())
                .size();

        final int numberOfRounds = (int) (Math.log(numberOfGamesInFirstRound) / Math.log(2)) + 1;

        return playoffGames.stream()
                .filter(g -> g.getPlayoff().getRound() == numberOfRounds || g.getPlayoff().getRound() == numberOfRounds + 1)
                .anyMatch(g -> g.getHomeUser() != null && g.getAwayUser() != null && g.getReporter() == null);
    }

    public boolean isFinalGame(Game game) {
        if (game.getTournament().getStatus() != TournamentStatus.PLAYOFFS || game.getPlayoff() == null) {
            return false;
        }

        final List<Game> playoffGames = gameRepository.findByTournamentAndPlayoffIsNotNull(game.getTournament());

        final int numberOfGamesInFirstRound = (int) playoffGames.stream()
                .filter(g -> g.getPlayoff().getRound() == 1)
                .count();

        final int numberOfRounds = (int) (Math.log(numberOfGamesInFirstRound) / Math.log(2)) + 1;
        return game.getPlayoff().getRound() >= numberOfRounds;
    }

    public Game advanceByGame(Game game) {
        if (isFinalGame(game)) {
            return null;
        }

        final User winner = game.getScoreHome() > game.getScoreAway() ? game.getHomeUser() : game.getAwayUser();

        int nextRound = game.getPlayoff().getRound() + 1;
        int nextSpot;
        boolean nextGameAsHomeUser;
        if (game.getPlayoff().getSpot() % 2 != 0) {
            nextSpot = (game.getPlayoff().getSpot() + 1) / 2;
            nextGameAsHomeUser = true;
        } else {
            nextSpot = game.getPlayoff().getSpot() / 2;
            nextGameAsHomeUser = false;
        }

        final Optional<Game> gameToAdvanceTo = gameRepository.findGameInPlayoffTree(
                game.getTournament(), nextSpot, nextRound);
        final Game persistedGameToAdvanceTo;

        if (gameToAdvanceTo.isPresent()) {
            if (gameToAdvanceTo.get().getHomeUser() == null) {
                gameToAdvanceTo.get().setHomeUser(winner);
            } else if (gameToAdvanceTo.get().getAwayUser() == null) {
                gameToAdvanceTo.get().setAwayUser(winner);
            } else {
                throw new RuntimeException("gameToAdvanceTo already has a home and away user.");
            }

            persistedGameToAdvanceTo = gameRepository.save(gameToAdvanceTo.get());
        } else {
            final Game newGameToAdvanceTo = new Game();

            if (nextGameAsHomeUser) {
                newGameToAdvanceTo.setHomeUser(winner);
            } else {
                newGameToAdvanceTo.setAwayUser(winner);
            }

            final PlayoffGame playoffGame = new PlayoffGame();
            playoffGame.setRound(nextRound);
            playoffGame.setSpot(nextSpot);
            newGameToAdvanceTo.setPlayoff(playoffGame);

            newGameToAdvanceTo.setTournament(game.getTournament());

            persistedGameToAdvanceTo = gameRepository.save(newGameToAdvanceTo);
        }

        return persistedGameToAdvanceTo;
    }
}
