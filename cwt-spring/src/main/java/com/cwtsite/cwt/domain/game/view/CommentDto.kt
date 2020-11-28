package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.Comment
import java.sql.Timestamp
import java.util.*

data class CommentDto (
        val id: Long,
        val body: String,
        val author: UserMinimalDto,
        val created: Date
) {

    companion object {

        fun toDto(comment: Comment) = CommentDto(
                id = comment.id!!,
                body = comment.body!!,
                author = UserMinimalDto.toDto(comment.author!!),
                created = Timestamp(comment.created!!.time)
        )
    }
}
