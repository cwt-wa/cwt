package com.cwtsite.cwt.flyway

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
class FlywayTestProfileTest {

    @Autowired
    private lateinit var dataSource: DataSource

    @PersistenceContext
    private lateinit var em: EntityManager

    @Test
    fun testExpectedMigrationResult() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.queryForList("select * from \"flyway_schema_history\"")
        Assert.assertEquals(0, em.createQuery("select u from User u", User::class.java).resultList.size.toLong())
        Assert.assertTrue(em.createQuery("select c from Configuration c", Configuration::class.java).resultList.size > 0)
    }

}
