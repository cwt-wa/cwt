package com.cwtsite.cwt.domain.tetris.repository.entity

import com.cwtsite.cwt.domain.user.repository.entity.User
import javax.persistence.*

@Entity
@Table(name = "tetris")
data class Tetris(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = null,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(nullable = true)
        val user: User,

        val highscore: Long
)