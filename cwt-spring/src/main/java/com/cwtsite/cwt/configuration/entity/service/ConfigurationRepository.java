package com.cwtsite.cwt.configuration.entity.service;

import com.cwtsite.cwt.configuration.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Configuration findByKey(String key);
}
