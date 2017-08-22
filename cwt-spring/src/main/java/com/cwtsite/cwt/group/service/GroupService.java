package com.cwtsite.cwt.group.service;

import com.cwtsite.cwt.entity.Tournament;
import com.cwtsite.cwt.group.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> save(List<Group> groups) {
        return groupRepository.save(groups);
    }

    public List<Group> getGroupsForTournament(final Tournament tournament) {
        return groupRepository.findByTournament(tournament);
    }
}
