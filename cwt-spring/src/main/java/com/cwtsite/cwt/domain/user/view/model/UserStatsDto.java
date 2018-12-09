package com.cwtsite.cwt.domain.user.view.model;

import com.cwtsite.cwt.domain.user.repository.entity.UserStats;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class UserStatsDto {

    private Boolean participated;
    private Integer year;
    private Long tournamentId;
    private Integer tournamentMaxRound;
    private Integer round;
    private String locRound;

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
            final int tournamentMaxRound = elem.get(2).asInt();
            final int round = elem.get(3).asInt();

            final UserStatsDto dto = new UserStatsDto();
            dto.setParticipated(elem.get(3).asInt() != 0);
            dto.setYear(elem.get(1).asInt());
            dto.setTournamentId(elem.get(0).asLong());
            dto.setTournamentMaxRound(tournamentMaxRound);
            dto.setRound(round);
            dto.setLocRound(locRound(tournamentMaxRound, round));
            dtos.add(dto);
        }

        return dtos;
    }

    static String locRound(int tournamentMaxRound, int round) {
        final Map<Integer, String> integerStringHashMap = new HashMap<>();
        int roundOfLastSixteen = tournamentMaxRound - 3;

        integerStringHashMap.put(tournamentMaxRound + 2, "Champion");
        integerStringHashMap.put(tournamentMaxRound + 1, "Runner-up");
        integerStringHashMap.put(tournamentMaxRound, "Third");
        integerStringHashMap.put(tournamentMaxRound - 1, "Semifinal");
        integerStringHashMap.put(tournamentMaxRound - 2, "Quarterfinal");
        integerStringHashMap.put(roundOfLastSixteen, "Last Sixteen");
        integerStringHashMap.put(1, "Group");
        integerStringHashMap.put(0, "Not attended");

        int multiplier = 2;
        for (int i = roundOfLastSixteen - 1; i > 1; i--) {
            integerStringHashMap.put(i, "Last " + Double.valueOf(16 * Math.pow(2, (multiplier - 1))).intValue());
            multiplier++;
        }

        return integerStringHashMap.get(round);
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

    public String getLocRound() {
        return locRound;
    }

    public void setLocRound(String locRound) {
        this.locRound = locRound;
    }
}
