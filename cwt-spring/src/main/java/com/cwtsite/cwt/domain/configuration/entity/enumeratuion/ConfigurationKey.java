package com.cwtsite.cwt.domain.configuration.entity.enumeratuion;

public enum ConfigurationKey {

    /**
     * Behold! The tournament rules!
     */
    RULES(ConfigurationValueType.STRING),

    /**
     * Number of group members advancing into the playoffs.
     */
    NUMBER_OF_GROUP_MEMBERS_ADVANCING(ConfigurationValueType.INTEGER),

    /**
     * What group stage games are played in.
     * I.e. 3 for possbile results of 2–1 and 2–0.
     */
    GROUP_GAMES_BEST_OF(ConfigurationValueType.INTEGER),

    /**
     * Like {@link ConfigurationKey#GROUP_GAMES_BEST_OF} but for the playoffs.
     */
    PLAYOFF_GAMES_BEST_OF(ConfigurationValueType.INTEGER),

    /**
     * Like {@link ConfigurationKey#GROUP_GAMES_BEST_OF} but for the final.
     */
    FINAL_GAME_BEST_OF(ConfigurationValueType.INTEGER),

    /**
     * News written by admins to be shown on the front page.
     */
    NEWS(ConfigurationValueType.STRING),

    /**
     * Relation of won rounds and points.
     * Defines comma-separated 2-tuples that relate how many points will be given for the amount of won rounds in a game.
     * <pre>
     * (won rounds, points), (won rounds, points), …
     * (3, 3), (2, 1)</pre>
     * Three points for three won rounds, one point for two won rounds. Anything else is zero points.
     */
    POINTS_PATTERN(ConfigurationValueType.STRING);


    private final ConfigurationValueType type;

    ConfigurationKey(final ConfigurationValueType valueType) {
        this.type = valueType;
    }

    public ConfigurationValueType getType() {
        return type;
    }

    private enum ConfigurationValueType {
        INTEGER, STRING;
    }
}

