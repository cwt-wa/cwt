package com.cwtsite.cwt.database

import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.*

open class AbstractDatabaseTest {

    @Autowired
    protected lateinit var em: TestEntityManager

    protected fun persistDummyUser(): User {
        val uuid = UUID.randomUUID().toString().substring(0, 16)
        return em.persist(User(email = "$uuid@cwtsite.com", username = uuid))
    }
}
