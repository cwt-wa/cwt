package com.cwtsite.cwt.game.view.model;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.group.entity.Group;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.user.repository.entity.User;

public class ReportDto {

    private Long user;
    private Long opponent;
    private Long scoreOfUser;
    private Long scoreOfOpponent;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getOpponent() {
        return opponent;
    }

    public void setOpponent(Long opponent) {
        this.opponent = opponent;
    }

    public Long getScoreOfUser() {
        return scoreOfUser;
    }

    public void setScoreOfUser(Long scoreOfUser) {
        this.scoreOfUser = scoreOfUser;
    }

    public Long getScoreOfOpponent() {
        return scoreOfOpponent;
    }

    public void setScoreOfOpponent(Long scoreOfOpponent) {
        this.scoreOfOpponent = scoreOfOpponent;
    }

    public static Game map(final ReportDto dto, final Tournament currentTournament,
                           final User reportingUser, final User opponent, final Group group) {
        final Game game = new Game();

        game.setScoreHome(Math.toIntExact(dto.getScoreOfUser()));
        game.setScoreAway(Math.toIntExact(dto.getScoreOfOpponent()));
        game.setTournament(currentTournament);
        game.setHomeUser(reportingUser);
        game.setAwayUser(opponent);
        game.setReporter(reportingUser);

        game.setGroup(group);


        return game;

    }
}
