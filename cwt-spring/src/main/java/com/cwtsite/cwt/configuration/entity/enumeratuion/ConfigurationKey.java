package com.cwtsite.cwt.configuration.entity.enumeratuion;

public enum ConfigurationKey {

    /**
     * Behold! The tournament rules!
     */
    RULES(String.class),

    /**
     * Number of participants in the group stage that play in the same group.
     */
    USERS_PER_GROUP(Integer.class),

    /**
     * Number of groups in the group stage.
     */
    NUMBER_OF_GROUPS(Integer.class);

    private final Class type;

    ConfigurationKey(final Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }
}
