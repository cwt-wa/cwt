package com.cwtsite.cwt.configuration.service;

import com.cwtsite.cwt.configuration.entity.Configuration;
import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConfigurationService {


    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Object getValue(ConfigurationKey configurationKey) {
        return configurationRepository.findByKey(configurationKey.getKey()).getValue();
    }

    public List<Configuration> getValues(List<ConfigurationKey> configurationKeys) {
        List<String> configurationKeysAsStrings = configurationKeys.stream()
                .map(ConfigurationKey::getKey)
                .collect(Collectors.toList());
        return configurationRepository.findByKeys(configurationKeysAsStrings);
    }

    public List<Configuration> getValues() {
        return configurationRepository.findAll();
    }
}
