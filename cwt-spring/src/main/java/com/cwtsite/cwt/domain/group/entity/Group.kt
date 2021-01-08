package com.cwtsite.cwt.domain.group.entity

import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.entity.GroupStanding
import javax.persistence.Column
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "\"group\"")
@SequenceGenerator(name = "group_seq", sequenceName = "group_id_seq", allocationSize = 1)
data class Group (

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_seq")
        var id: Long? = null,

        @Enumerated(EnumType.STRING)
        @Column(name = "label")
        var label: GroupLabel? = null,

        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var tournament: Tournament? = null,

        @OneToMany(cascade = [CascadeType.ALL])
        @JoinColumn(name = "group_id")
        val standings: MutableList<GroupStanding> = mutableListOf()
) {

    override fun toString(): String = "Group{id=$id, labeel=$label}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
