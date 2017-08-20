package com.cwtsite.cwt.configuration.service;

import com.cwtsite.cwt.configuration.entity.Configuration;
import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Configuration findByKey(String key);

    List<Configuration> findByKeys(List<String> keys);
}
