package com.cwtsite.cwt.domain.configuration.entity;


import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "api/rules")
public class RulesRestController {

    private final ConfigurationService configurationService;

    @Autowired
    public RulesRestController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public String[] getCurrentRules() {
        @SuppressWarnings("ConstantConditions")
        Configuration configuration = configurationService.getOne(ConfigurationKey.RULES);
        return new String[]{configuration.getValue()};
    }
}
