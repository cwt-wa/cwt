package com.cwtsite.cwt.tournament.view.model;

import java.util.List;

public class StartNewTournamentDto {

    private List<Long> moderatorIds;

    public List<Long> getModeratorIds() {
        return moderatorIds;
    }

    public void setModeratorIds(List<Long> moderatorIds) {
        this.moderatorIds = moderatorIds;
    }
}
