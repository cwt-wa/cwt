package com.cwtsite.cwt.game.view.model;

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
}
