package com.cwtsite.cwt.integration

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.test.context.TestPropertySource
import java.lang.annotation.Inherited

@Inherited
@MustBeDocumented
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@FlywayTest(locationsForMigrate = ["classpath:db/migration/common", "classpath:db/migration/test"], overrideLocations = true)
@TestPropertySource(properties = [
    "spring.flyway.locations=classpath:db/migration/common,classpath:db/migration/test",
    "spring.jpa.properties.hibernate.default_schema=public"
])
@AutoConfigureEmbeddedDatabase
annotation class EmbeddedPostgres
