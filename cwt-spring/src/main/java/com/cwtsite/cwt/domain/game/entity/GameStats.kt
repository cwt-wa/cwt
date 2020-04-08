package com.cwtsite.cwt.domain.game.entity

import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "game_stats")
@SequenceGenerator(name = "game_stats_seq", sequenceName = "game_stats_id_seq", allocationSize = 5)
data class GameStats(

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_stats_seq")
        var id: Long? = null,

        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var game: Game? = null,

        @Column(name = "data", columnDefinition = "text")
        var data: String,

        @Column(name = "started_at")
        var startedAt: Timestamp? = null,

        @field:CreationTimestamp
        @Column(name = "created", nullable = false, updatable = false)
        var created: Timestamp? = null
)
