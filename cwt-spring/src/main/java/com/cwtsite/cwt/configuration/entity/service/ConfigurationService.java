package com.cwtsite.cwt.configuration.entity.service;

import com.cwtsite.cwt.configuration.entity.Configuration;
import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
