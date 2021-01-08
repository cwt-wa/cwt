package com.cwtsite.cwt.domain.game.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

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
        var startedAt: Instant? = null,

        @Column(name = "map")
        var map: String? = null,

        @Column(name = "texture")
        var texture: String? = null,

        @field:CreationTimestamp
        @Column(name = "created", nullable = false, updatable = false)
        var created: Instant? = null

) {

        override fun toString(): String {
                return "GameStats{id=$id}"
        }

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as GameStats

                if (id != other.id) return false

                return true
        }

        override fun hashCode(): Int {
                return id?.hashCode() ?: 0
        }
}

