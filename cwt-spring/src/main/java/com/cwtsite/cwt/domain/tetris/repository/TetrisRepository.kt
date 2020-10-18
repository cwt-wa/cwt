package com.cwtsite.cwt.domain.tetris.repository

import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TetrisRepository : JpaRepository<Tetris, Long>
