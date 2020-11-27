package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.Comment

data class CommentDto (
        val id: Long,
        val body: String,
        val user: UserMinimalDto
) {

    companion object {

        fun toDto(comment: Comment) = CommentDto(
                id = comment.id!!,
                body = comment.body!!,
                user = UserMinimalDto.toDto(comment.author!!))
    }
}
