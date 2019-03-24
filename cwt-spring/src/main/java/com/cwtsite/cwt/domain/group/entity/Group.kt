package com.cwtsite.cwt.domain.group.entity

import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.entity.GroupStanding
import javax.persistence.*

@Entity
@Table(name = "\"group\"")
@SequenceGenerator(name = "group_seq", sequenceName = "group_seq")
data class Group(

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

//        @OneToMany(cascade = [CascadeType.ALL])
//        val games: MutableList<Game> = mutableListOf()
)
