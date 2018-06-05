package com.cwtsite.cwt.domain.game.view;

import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType;

public class RatingDto {

    private Long user;
    private RatingType type;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public RatingType getType() {
        return type;
    }

    public void setType(RatingType type) {
        this.type = type;
    }
}
