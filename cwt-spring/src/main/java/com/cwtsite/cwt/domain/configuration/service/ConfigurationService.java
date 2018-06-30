package com.cwtsite.cwt.domain.configuration.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConfigurationService {


    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Optional<Configuration> getOne(ConfigurationKey configurationKey) {
        return configurationRepository.findById(configurationKey);
    }

    public List<Configuration> getAll(List<ConfigurationKey> configurationKeys) {
        return configurationRepository.findAllById(configurationKeys);
    }

    public List<Configuration> getAll() {
        return configurationRepository.findAll();
    }

    public Configuration save(Configuration configuration) {
        return this.configurationRepository.save(configuration);
    }
}
