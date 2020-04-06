package com.cwtsite.cwt.domain.configuration.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public List<int[]> getParsedPointsPatternConfiguration() {
        final String rawWithoutWhiteSpace = getOne(ConfigurationKey.POINTS_PATTERN).getValue().replaceAll(" ", "");
        final Matcher matcher = Pattern.compile("\\((\\d+,\\d+)\\)").matcher(rawWithoutWhiteSpace);

        final List<int[]> pointsPattern = new ArrayList<>();

        while (matcher.find()) {
            final String[] split = matcher.group(1).split(",");

            if ("0".equals(split[1])) {
                continue;
            }

            pointsPattern.add(new int[]{Integer.valueOf(split[0]), Integer.valueOf(split[1])});
        }

        return pointsPattern;
    }

    public Configuration getOne(ConfigurationKey configurationKey) {
        //noinspection OptionalGetWithoutIsPresent
        return configurationRepository.findById(configurationKey).get();
    }

    public List<Configuration> findAll(List<ConfigurationKey> configurationKeys) {
        return configurationRepository.findAllById(configurationKeys);
    }

    public List<Configuration> findAll() {
        return configurationRepository.findAll();
    }

    public Configuration save(Configuration configuration) {
        return this.configurationRepository.save(configuration);
    }
}
