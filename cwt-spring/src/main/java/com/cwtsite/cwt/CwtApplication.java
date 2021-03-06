package com.cwtsite.cwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(MailProperties.class)
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableScheduling
public class CwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(CwtApplication.class, args);
    }
}

