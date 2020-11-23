package com.cwtsite.cwt.domain.game.entity

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Formula
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "game")
@SequenceGenerator(name = "game_seq", sequenceName = "game_id_seq", allocationSize = 5)
data class Game(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_seq")
        var id: Long? = null,

        @Column(name = "score_home")
        var scoreHome: Int? = null,

        @Column(name = "score_away")
        var scoreAway: Int? = null,

        @Column(name = "tech_win")
        var techWin: Boolean = false,

        @Column(name = "reported_at")
        var reportedAt: Timestamp? = null,

        @Column(name = "replay_quantity")
        var replayQuantity: Int? = null,

        @field:CreationTimestamp
        @Column(name = "created", nullable = false, updatable = false)
        var created: Timestamp? = null,

        @field:UpdateTimestamp
        @Column(name = "modified", nullable = false)
        var modified: Timestamp? = null,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(unique = true)
        var playoff: PlayoffGame? = null,

        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var tournament: Tournament,

        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "group_id")
        var group: Group? = null,

        @ManyToOne
        var homeUser: User? = null,

        @ManyToOne
        var awayUser: User? = null,

        @ManyToOne
        @JoinColumn
        var reporter: User? = null,

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "game")
        val ratings: List<Rating> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "game")
        val comments: List<Comment> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "game")
        val bets: List<Bet> = mutableListOf(),

        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(unique = true)
        var replay: Replay? = null,

        @Basic(fetch = FetchType.LAZY)
        @Formula("(select count(*) from COMMENT c where c.GAME_ID = id)")
        val commentsSize: Int? = null,

        @Basic(fetch = FetchType.LAZY)
        @Formula("(select count(*) from RATING r where r.GAME_ID = id)")
        val ratingsSize: Int? = null,

        var voided: Boolean = false
) {

    fun pairingInvolves(user: User?) = (user != null) && (homeUser == user || awayUser == user)

    fun pairingCompleted() = homeUser != null && awayUser != null

    fun pairingHalfCompleted() = (homeUser == null) xor (awayUser == null)

    fun pairingEmpty() = homeUser == null && awayUser == null

    fun pairUser(user: User) {
        if (!pairingHalfCompleted()) throw RuntimeException("Game is not half paired.")
        if (homeUser == null) homeUser = user else awayUser = user
    }

    fun wasPlayed() = scoreHome != null && scoreAway != null && homeUser != null && awayUser != null

    fun winner() = (if (scoreHome!! > scoreAway!!) homeUser else awayUser)!!

    fun loser() = (if (scoreHome!! > scoreAway!!) awayUser else homeUser)!!

    fun playoff() = playoff != null && group == null

    fun group() = !playoff()

    override fun toString(): String {
        return "Game(id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
