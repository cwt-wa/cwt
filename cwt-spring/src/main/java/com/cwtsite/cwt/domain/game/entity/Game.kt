package com.cwtsite.cwt.domain.game.entity

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
@SequenceGenerator(name = "game_seq", sequenceName = "game_seq")
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

        @Column(name = "downloads")
        var downloads: Int? = null,

        @field:CreationTimestamp
        @Column(name = "created", nullable = false)
        var created: Timestamp? = null,

        @field:UpdateTimestamp
        @Column(name = "modified", nullable = false)
        var modified: Timestamp? = null,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(unique = true)
        var playoff: PlayoffGame? = null,

        @ManyToOne
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
        var reporter: User? = null,

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "game")
        val ratings: List<Rating> = mutableListOf(),

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "game")
        val comments: List<Comment> = mutableListOf(),

        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
        var replay: Replay? = null,

        @Basic(fetch = FetchType.LAZY)
        @Formula("(select count(*) from COMMENT c where c.GAME_ID = id)")
        val commentsSize: Int? = null,

        @Basic(fetch = FetchType.LAZY)
        @Formula("(select count(*) from RATING r where r.GAME_ID = id)")
        val ratingsSize: Int? = null,

        var voided: Boolean = false
) {

    fun wasPlayedBy(user: User) = homeUser == user || awayUser == user
}
