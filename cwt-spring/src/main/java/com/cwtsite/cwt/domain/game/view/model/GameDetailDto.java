package com.cwtsite.cwt.domain.game.view.model;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.PlayoffGame;
import com.cwtsite.cwt.domain.game.entity.Rating;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.Comment;

import java.util.Date;
import java.util.List;

public class GameDetailDto {

    private Long id;
    private Integer scoreHome;
    private Integer scoreAway;
    private Boolean techWin;
    private Integer downloads;
    private Date created;
    private Date modified;
    private PlayoffGame playoff;
    private Group group;
    private Tournament tournament;
    private User homeUser;
    private User awayUser;
    private User reporter;
    private List<Rating> ratings;
    private List<Comment> comments;
    private boolean replayExists;

    public static GameDetailDto toDto(Game game) {
        final GameDetailDto dto = new GameDetailDto();

        dto.setId(game.getId());
        dto.setScoreHome(game.getScoreHome());
        dto.setScoreAway(game.getScoreAway());
        dto.setTechWin(game.getTechWin());
        dto.setDownloads(game.getDownloads());
        dto.setCreated(game.getCreated());
        dto.setModified(game.getModified());
        dto.setPlayoff(game.getPlayoff());
        dto.setGroup(game.getGroup());
        dto.setTournament(game.getTournament());
        dto.setHomeUser(game.getHomeUser());
        dto.setAwayUser(game.getAwayUser());
        dto.setReporter(game.getReporter());
        dto.setRatings(game.getRatings());
        dto.setComments(game.getComments());
        dto.setReplayExists(game.getReplay() != null);

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScoreHome() {
        return scoreHome;
    }

    public void setScoreHome(Integer scoreHome) {
        this.scoreHome = scoreHome;
    }

    public Integer getScoreAway() {
        return scoreAway;
    }

    public void setScoreAway(Integer scoreAway) {
        this.scoreAway = scoreAway;
    }

    public Boolean getTechWin() {
        return techWin;
    }

    public void setTechWin(Boolean techWin) {
        this.techWin = techWin;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public PlayoffGame getPlayoff() {
        return playoff;
    }

    public void setPlayoff(PlayoffGame playoff) {
        this.playoff = playoff;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public User getHomeUser() {
        return homeUser;
    }

    public void setHomeUser(User homeUser) {
        this.homeUser = homeUser;
    }

    public User getAwayUser() {
        return awayUser;
    }

    public void setAwayUser(User awayUser) {
        this.awayUser = awayUser;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean isReplayExists() {
        return replayExists;
    }

    public void setReplayExists(boolean replayExists) {
        this.replayExists = replayExists;
    }
}
