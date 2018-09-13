package com.cwtsite.cwt.domain.game.view.model;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.PlayoffGame;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;

public class GameDto {

    private Long id;
    private Long homeUser;
    private Long awayUser;
    private PlayoffDto playoff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public static Game fromDto(final GameDto dto, final User home, final User away,
                               final Tournament tournament) {
        final Game game = new Game();

        game.setId(dto.getId());
        game.setHomeUser(home);
        game.setAwayUser(away);
        game.setTournament(tournament);

        final PlayoffGame playoffGame = new PlayoffGame();
        playoffGame.setRound(dto.getPlayoff().getRound());
        playoffGame.setSpot(dto.getPlayoff().getSpot());
        game.setPlayoff(playoffGame);

        return game;
    }

    public static GameDto toDto(Game game) {
        final GameDto dto = new GameDto();

        dto.setId(game.getId());
        dto.setHomeUser(game.getHomeUser().getId());
        dto.setAwayUser(game.getAwayUser().getId());
        dto.setPlayoff(PlayoffDto.toDto(game.getPlayoff()));

        return dto;
    }
}
