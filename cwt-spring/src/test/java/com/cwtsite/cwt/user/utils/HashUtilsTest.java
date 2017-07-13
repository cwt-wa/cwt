package com.cwtsite.cwt.user.utils;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

public class HashUtilsTest {

    @Test
    public void createHash() throws Exception {
        Assert.assertEquals("2ca5f31ba55250aaf937c0fbb63a498c6c8f14b4", HashUtils.createHash("1LikeMyPassword();"));
    }
}
