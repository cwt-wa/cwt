package com.cwtsite.cwt.domain.user.service;

import com.cwtsite.cwt.domain.user.view.model.JwtUser;

import java.io.Serializable;

public class JwtTokenContext implements Serializable {

    private static final long serialVersionUID = 1L;

    public final JwtUser<?> user;

    JwtTokenContext(JwtUser<?> jwtUser) {
        user = jwtUser;
    }

    public JwtUser<?> getUser() {
        return user;
    }
}
