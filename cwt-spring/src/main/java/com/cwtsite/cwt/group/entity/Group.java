package com.cwtsite.cwt.group.entity;

import com.cwtsite.cwt.entity.GroupStanding;
import com.cwtsite.cwt.entity.Tournament;
import com.cwtsite.cwt.group.entity.enumeration.GroupLabel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A Group.
 */
@Entity
@Table(name = "\"group\"")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "label")
    private GroupLabel label;

    @ManyToOne
    private Tournament tournament;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "group")
    private List<GroupStanding> standings;

    public Group() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroupLabel getLabel() {
        return label;
    }

    public void setLabel(GroupLabel label) {
        this.label = label;
    }

    public Group label(GroupLabel label) {
        this.label = label;
        return this;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Group tournament(Tournament tournament) {
        this.tournament = tournament;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Group group = (Group) o;
        if (group.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, group.id);
    }

    public List<GroupStanding> getStandings() {
        return standings;
    }

    public void setStandings(List<GroupStanding> standings) {
        this.standings = standings;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", label='" + label + "'" +
                '}';
    }
}
