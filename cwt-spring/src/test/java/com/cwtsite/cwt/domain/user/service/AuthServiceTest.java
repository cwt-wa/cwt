package com.cwtsite.cwt.domain.user.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class AuthServiceTest {

    @Test
    public void createLegacyHash() {
        final AuthService authService = Mockito.mock(AuthService.class);
        ReflectionTestUtils.setField(authService, "salt", "UQDSPKa7nnnau6s67ZGS1ighLRL37rUDF2InTCTlgjItxgQsBR1YMol6AVIsfMn");
        Mockito.when(authService.createLegacyHash("dev")).thenCallRealMethod();
        Assert.assertEquals("51bf293b2a7ebf5d5699e5f54ff9ac7e8ab1cf7b", authService.createLegacyHash("dev"));
    }
}
