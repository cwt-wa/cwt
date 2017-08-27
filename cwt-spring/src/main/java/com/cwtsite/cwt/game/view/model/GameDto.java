package com.cwtsite.cwt.game.view.model;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.game.entity.PlayoffGame;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.user.repository.entity.User;

public class GameDto {

    private Long homeUser;
    private Long awayUser;
    private PlayoffDto playoff;

    public Long getHomeUser() {
        return homeUser;
    }

    public void setHomeUser(Long homeUser) {
        this.homeUser = homeUser;
    }

    public Long getAwayUser() {
        return awayUser;
    }

    public void setAwayUser(Long awayUser) {
        this.awayUser = awayUser;
    }

    public PlayoffDto getPlayoff() {
        return playoff;
    }

    public void setPlayoff(PlayoffDto playoff) {
        this.playoff = playoff;
    }

    public static Game map(final GameDto dto, final User home, final User away,
                           final Tournament tournament) {
        final Game game = new Game();

        game.setHomeUser(home);
        game.setAwayUser(away);
        game.setTournament(tournament);

        final PlayoffGame playoffGame = new PlayoffGame();
        playoffGame.setRound(dto.getPlayoff().getRound());
        playoffGame.setSpot(dto.getPlayoff().getSpot());
        game.setPlayoff(playoffGame);

        return game;
    }
}
