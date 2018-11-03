package com.cwtsite.cwt.domain.configuration.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {

    @InjectMocks
    private ConfigurationService configurationService;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Test
    public void getParsedPointsPatternConfiguration() {
        Mockito
                .when(configurationRepository.findById(ConfigurationKey.POINTS_PATTERN))
                .thenReturn(Optional.of(createPointsPatternConfiguration("(3, 3), (2, 1)")))
                .thenReturn(Optional.of(createPointsPatternConfiguration("(10, 12),(0, 1),(2  , 1), (0,0),")));

        Assert.assertArrayEquals(
                new int[][]{new int[]{3, 3}, new int[]{2, 1}},
                configurationService.getParsedPointsPatternConfiguration().toArray());

        Assert.assertArrayEquals(
                new int[][]{
                        new int[]{10, 12},
                        new int[]{0, 1},
                        new int[]{2, 1}},
                configurationService.getParsedPointsPatternConfiguration().toArray());
    }

    private Configuration createPointsPatternConfiguration(String value) {
        final Configuration configuration = new Configuration();
        configuration.setValue(value);
        configuration.setKey(ConfigurationKey.POINTS_PATTERN);
        return configuration;
    }
}
