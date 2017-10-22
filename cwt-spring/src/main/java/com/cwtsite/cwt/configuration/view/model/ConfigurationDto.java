package com.cwtsite.cwt.configuration.view.model;

import com.cwtsite.cwt.configuration.entity.Configuration;
import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.user.repository.entity.User;

public class ConfigurationDto {

    private String value;
    private ConfigurationKey key;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ConfigurationKey getKey() {
        return key;
    }

    public void setKey(ConfigurationKey key) {
        this.key = key;
    }

    public static Configuration map(ConfigurationDto configurationDto, User author) {
        final Configuration configuration = new Configuration();
        configuration.setKey(configurationDto.getKey());
        configuration.setValue(configurationDto.getValue());
        configuration.setAuthor(author);
        return configuration;
    }
}
