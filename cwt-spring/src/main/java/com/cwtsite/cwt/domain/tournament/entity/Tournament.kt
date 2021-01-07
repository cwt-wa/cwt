package com.cwtsite.cwt.domain.tournament.entity

import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
data class Tournament(

        @Id
        @SequenceGenerator(name = "tournament_seq", sequenceName = "tournament_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_seq")
        val id: Long? = null,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var status: TournamentStatus = TournamentStatus.OPEN,

        @Column(columnDefinition = "text")
        var review: String? = null,

        /**
         * During the tournament the value can be calculated from the configuration values
         * but once the tournament is [finished][TournamentStatus.FINISHED] the value should be remembered in this column.
         *
         * [Calculation of the value][com.cwtsite.cwt.domain.playoffs.service.TreeService.getNumberOfPlayoffRoundsInTournament]
         */
        @Column(nullable = false)
        var maxRounds: Int = 5,

        @Column(nullable = false)
        var numOfGroupAdvancing: Int = 2,

        @Column
        var threeWay: Boolean? = null,

        @Column(name = "created", nullable = false)
        @field:CreationTimestamp
        var created: Instant? = null,

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
        var moderators: MutableSet<User> = mutableSetOf()
) {
    override fun toString() = "Tournament{id=$id, created=$created, status=$status}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tournament

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
