package com.cwtsite.cwt.domain.game.entity

import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "game_stats")
data class GameStats(

        @Id
        @Column(name = "game_id")
        var gameId: Long? = null,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "game_id", referencedColumnName = "id")
        var game: Game? = null,

        @Column(name = "data", columnDefinition = "text")
        var data: String,

        @Column(name = "started_at")
        var startedAt: Timestamp? = null,

        @field:CreationTimestamp
        @Column(name = "created", nullable = false, updatable = false)
        var created: Timestamp? = null
)
