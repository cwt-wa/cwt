package com.cwtsite.cwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableConfigurationProperties(MailProperties.class)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class CwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(CwtApplication.class, args);
    }
}
