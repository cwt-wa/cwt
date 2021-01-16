package com.cwtsite.cwt.domain.game.view.mapper

import com.cwtsite.cwt.domain.game.view.model.CommentDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.Comment

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class CommentMapper {


    fun toDto(comment: Comment) = CommentDto(
            id = comment.id!!,
            body = comment.body!!,
            author = UserMinimalDto(id = comment.author!!.id!!, username = comment.author!!.username),
            created = comment.created!!
    )
}

