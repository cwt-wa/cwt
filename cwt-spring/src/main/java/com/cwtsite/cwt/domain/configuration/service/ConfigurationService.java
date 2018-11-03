package com.cwtsite.cwt.domain.configuration.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.exception.IllegalTournamentStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConfigurationService {


    private final ConfigurationRepository configurationRepository;
    private final PlayoffService playoffService;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository, PlayoffService playoffService) {
        this.configurationRepository = configurationRepository;
        this.playoffService = playoffService;
    }

    public Configuration getBestOfValue(TournamentStatus tournamentStatus) {
        ConfigurationKey configurationKey;

        if (tournamentStatus == TournamentStatus.GROUP) {
            configurationKey = ConfigurationKey.GROUP_GAMES_BEST_OF;
        } else if (tournamentStatus == TournamentStatus.PLAYOFFS) {
            configurationKey = playoffService.finalGamesAreNext()
                    ? ConfigurationKey.FINAL_GAME_BEST_OF
                    : ConfigurationKey.PLAYOFF_GAMES_BEST_OF;
        } else {
            throw new IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS);
        }

        return getOne(configurationKey);
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
