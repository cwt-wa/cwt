package com.cwtsite.cwt.domain.game.view.model;

import com.cwtsite.cwt.domain.game.entity.PlayoffGame;

public class PlayoffDto {

    private Integer round;
    private Integer spot;

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getSpot() {
        return spot;
    }

    public void setSpot(Integer spot) {
        this.spot = spot;
    }

    public static PlayoffDto toDto(PlayoffGame playoffGame) {
        final PlayoffDto dto = new PlayoffDto();

        dto.setRound(playoffGame.getRound());
        dto.setSpot(playoffGame.getSpot());

        return dto;
    }
}
