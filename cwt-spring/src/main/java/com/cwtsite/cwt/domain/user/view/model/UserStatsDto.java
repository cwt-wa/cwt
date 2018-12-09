package com.cwtsite.cwt.domain.user.view.model;

import com.cwtsite.cwt.domain.user.repository.entity.UserStats;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserStatsDto {

    private Boolean participated;
    private Integer year;
    private Long tournamentId;
    private Integer tournamentMaxRound;
    private Integer round;

    public static List<UserStatsDto> toDtos(UserStats userStats) {
        final Iterator<JsonNode> timelineIterator;
        try {
            timelineIterator = new ObjectMapper().readTree("[" + userStats.getTimeline() + "]").elements();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ArrayList<UserStatsDto> dtos = new ArrayList<>();
        while (timelineIterator.hasNext()) {
            JsonNode elem = timelineIterator.next();

            final UserStatsDto dto = new UserStatsDto();
            dto.setParticipated(elem.get(3).asInt() != 0);
            dto.setYear(elem.get(1).asInt());
            dto.setTournamentId(elem.get(0).asLong());
            dto.setTournamentMaxRound(elem.get(2).asInt());
            dto.setRound(elem.get(3).asInt());
            dtos.add(dto);
        }

        return dtos;
    }

    public Boolean getParticipated() {
        return participated;
    }

    public void setParticipated(Boolean participated) {
        this.participated = participated;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getTournamentMaxRound() {
        return tournamentMaxRound;
    }

    public void setTournamentMaxRound(Integer tournamentMaxRound) {
        this.tournamentMaxRound = tournamentMaxRound;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }
}
