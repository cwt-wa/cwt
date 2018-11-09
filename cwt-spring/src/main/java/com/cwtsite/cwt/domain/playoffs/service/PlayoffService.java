package com.cwtsite.cwt.domain.playoffs.service;

import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.PlayoffGame;
import com.cwtsite.cwt.domain.game.service.GameRepository;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlayoffService {

    private final GameRepository gameRepository;
    private final TournamentService tournamentService;
    private final GroupRepository groupRepository;
    private final ConfigurationService configurationService;

    @Autowired
    public PlayoffService(GameRepository gameRepository, TournamentService tournamentService,
                          GroupRepository groupRepository, ConfigurationService configurationService) {
        this.gameRepository = gameRepository;
        this.tournamentService = tournamentService;
        this.groupRepository = groupRepository;
        this.configurationService = configurationService;
    }

    public List<Game> getGamesOfTournament(final Tournament tournament) {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournament);
    }

    public Game getNextGameForUser(final User user) {
        return gameRepository.findNextPlayoffGameForUser(tournamentService.getCurrentTournament(), user);
    }

    public boolean onlyFinalGamesAreLeftToPlay() {
        final Tournament currentTournament = tournamentService.getCurrentTournament();

        if (currentTournament.getStatus() != TournamentStatus.PLAYOFFS) {
            return false;
        }

        final List<Game> finalGames = gameRepository.findReadyGamesInRoundEqualOrGreaterThan(
                getNumberOfPlayoffRoundsInTournament(currentTournament));

        return finalGames.size() == 2;
    }

    private boolean isFinalGame(Game game) {
        return game.getPlayoff().getRound() == getNumberOfPlayoffRoundsInTournament(game.getTournament()) + 1;
    }

    private boolean isThirdPlaceGame(Game game) {
        return game.getPlayoff().getRound() == getNumberOfPlayoffRoundsInTournament(game.getTournament());
    }

    private int getNumberOfPlayoffRoundsInTournament(Tournament tournament) {
        final int groupsCount = groupRepository.countByTournament(tournament);
        final int numberOfGroupMembersAdvancing = Integer.parseInt(
                configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING).getValue());

        return (int) (Math.log(groupsCount * numberOfGroupMembersAdvancing) / Math.log(2));
    }

    public Game advanceByGame(Game game) {
        if (isFinalGame(game) || isThirdPlaceGame(game)) {
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
                game.getTournament(), nextRound, nextSpot);
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
