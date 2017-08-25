package com.cwtsite.cwt.configuration.entity.enumeratuion;

public enum ConfigurationKey {

    /**
     * Behold! The tournament rules!
     */
    RULES(String.class),

    /**
     * Number of group members advancing into the playoffs.
     */
    NUMBER_OF_GROUP_MEMBERS_ADVANCING(Integer.class);

    private final Class type;

    ConfigurationKey(final Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }
}
