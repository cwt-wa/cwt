package com.cwtsite.cwt.game.view.model;

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
}
