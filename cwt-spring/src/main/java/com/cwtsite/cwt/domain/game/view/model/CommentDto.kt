package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.Comment
import java.time.Instant

data class CommentDto (
        val id: Long,
        val body: String,
        val author: UserMinimalDto,
        val created: Instant
) 

