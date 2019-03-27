package com.cwtsite.cwt.domain.tournament.entity

import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
data class Tournament(

        @Id
        @SequenceGenerator(name = "tournament_seq", sequenceName = "tournament_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_seq")
        val id: Long? = null,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var status: TournamentStatus = TournamentStatus.OPEN,

        @Column(columnDefinition = "text")
        var review: String? = null,

        @Column
        var maxRounds: Int? = null,

        @Column(name = "created", nullable = false)
        @field:CreationTimestamp
        var created: Timestamp? = null,

        @ManyToOne
        var bronzeWinner: User? = null,

        @ManyToOne
        var silverWinner: User? = null,

        @ManyToOne
        var goldWinner: User? = null,

        @ManyToMany
        @JoinTable(name = "tournament_moderator",
                joinColumns = [JoinColumn(name = "tournaments_id", referencedColumnName = "ID")],
                inverseJoinColumns = [JoinColumn(name = "moderators_id", referencedColumnName = "ID")])
        var moderators: Set<User> = HashSet()
)
