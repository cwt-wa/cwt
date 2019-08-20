package com.cwtsite.cwt.domain.bet.entity

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "bet")
@SequenceGenerator(name = "bet_seq", sequenceName = "bet_id_seq")
data class Bet(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bet_seq")
        var id: Long? = null,

        @ManyToOne(optional = false, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
        @JoinColumn(nullable = false)
        val user: User,

        @ManyToOne(optional = false, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
        @JoinColumn(nullable = false)
        val game: Game,

        @Column(nullable = false)
        var betOnHome: Boolean,

        @field:UpdateTimestamp
        @Column(nullable = false)
        var modified: Timestamp? = null
)
