package com.cwtsite.cwt.configuration.entity.enumeratuion;

public enum ConfigurationKey {

    /**
     * Behold! The tournament rules!
     */
    RULES(String.class),

    /**
     * Number of group members advancing into the playoffs.
     */
    NUMBER_OF_GROUP_MEMBERS_ADVANCING(Integer.class),

    /**
     * What group stage games are played in.
     * I.e. 3 for possbile results of 2–1 and 2–0.
     */
    GROUP_GAMES_BEST_OF(Integer.class),

    /**
     * Like {@link ConfigurationKey#GROUP_GAMES_BEST_OF} but for the playoffs.
     */
    PLAYOFF_GAMES_BEST_OF(Integer.class),

    /**
     * Like {@link ConfigurationKey#GROUP_GAMES_BEST_OF} but for the game for the third place.
     */
    LITTLE_FINAL_GAME_BEST_OF(Integer.class),

    /**
     * Like {@link ConfigurationKey#GROUP_GAMES_BEST_OF} but for the final.
     */
    FINAL_GAME_BEST_OF(Integer.class);


    private final Class type;

    ConfigurationKey(final Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }
}
