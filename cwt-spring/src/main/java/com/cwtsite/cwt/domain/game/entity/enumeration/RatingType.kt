package com.cwtsite.cwt.domain.game.entity.enumeration


enum class RatingType {

    DARKSIDE, LIGHTSIDE, LIKE, DISLIKE;

    fun opposite(): RatingType =
            when (this) {
                DARKSIDE -> LIGHTSIDE
                LIGHTSIDE -> DARKSIDE
                LIKE -> DISLIKE
                DISLIKE -> LIKE
            }
}
