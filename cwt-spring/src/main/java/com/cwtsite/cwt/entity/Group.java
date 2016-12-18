package com.cwtsite.cwt.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Group.
 */
@Entity
@Table(name = "\"group\"")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "label")
    private String label;

    @ManyToOne
    private Tournament tournament;

    protected Group() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Group label(String label) {
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
