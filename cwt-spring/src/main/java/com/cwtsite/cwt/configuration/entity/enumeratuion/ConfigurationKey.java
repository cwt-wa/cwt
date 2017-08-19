package com.cwtsite.cwt.configuration.entity.enumeratuion;

public enum ConfigurationKey {

    /**
     * Behold! The tournament rules!
     */
    RULES("rules", String.class);

    private final String key;
    private final Class type;

    ConfigurationKey(final String key, final Class type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public Class getType() {
        return type;
    }
}
