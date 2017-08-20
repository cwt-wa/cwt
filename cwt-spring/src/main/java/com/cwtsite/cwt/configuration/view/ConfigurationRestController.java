package com.cwtsite.cwt.configuration.view;

import com.cwtsite.cwt.configuration.entity.Configuration;
import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.configuration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/configuration")
public class ConfigurationRestController {


    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationRestController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public List<Configuration> query(@RequestParam(value = "keys", required = false) List<ConfigurationKey> configurationKeys) {
        if (configurationKeys == null) {
            return configurationService.getAll();
        }

        return configurationService.getAll(configurationKeys);
    }
}
