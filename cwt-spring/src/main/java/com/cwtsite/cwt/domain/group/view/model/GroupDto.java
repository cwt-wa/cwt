package com.cwtsite.cwt.domain.group.view.model;

import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.GroupStanding;

import java.util.List;
import java.util.stream.Collectors;

public class GroupDto {

    private GroupLabel label;
    private List<Long> users;

    public GroupLabel getLabel() {
        return label;
    }

    public void setLabel(GroupLabel label) {
        this.label = label;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public static Group map(final Tournament tournament, final List<User> groupMembers, GroupLabel label) {
        Group group = new Group();

        group.setLabel(label);
        group.setTournament(tournament);

        List<GroupStanding> standings = groupMembers.stream()
                .map(user -> new GroupStanding(group, user))
                .collect(Collectors.toList());

        group.setStandings(standings);

        return group;
    }

    public static GroupDto toDto(Group group) {
        final GroupDto dto = new GroupDto();

        dto.setLabel(group.getLabel());
        dto.setUsers(group.getStandings().stream().map(gs -> gs.getUser().getId()).collect(Collectors.toList()));

        return dto;
    }
}
