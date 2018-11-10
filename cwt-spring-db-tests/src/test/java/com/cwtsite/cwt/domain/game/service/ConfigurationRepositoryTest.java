package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.AbstractDbTest;

import java.util.List;

public class ConfigurationRepositoryTest extends AbstractDbTest {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    public void testFindAll() {
        final List<Configuration> configurations = configurationRepository.findAll();
        final ConfigurationKey[] configurationKeys = ConfigurationKey.values();

        for (ConfigurationKey configurationKey : configurationKeys) {
            final Configuration configuration = configurations.stream()
                    .filter(c -> c.getKey() == configurationKey)
                    .findFirst().orElseThrow(RuntimeException::new);

            parseValue(configuration.getValue(), configuration.getKey().getType());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void parseValue(String value, ConfigurationKey.ConfigurationValueType type) {
        switch (type) {
            case INTEGER:
                Integer.parseInt(value);
                break;
            case STRING:
                String.valueOf(value);
                break;
        }
    }

}
