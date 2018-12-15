package test

import com.btc.redg.generated.GUser
import com.btc.redg.generated.RedG
import com.btc.redg.runtime.AbstractRedG
import com.btc.redg.runtime.RedGEntity
import com.btc.redg.runtime.dummy.DefaultDummyFactory
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.sql.SQLException
import javax.sql.DataSource

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
abstract class AbstractDbTest {

    @Autowired
    protected lateinit var dataSource: DataSource

    @Autowired
    protected lateinit var em: TestEntityManager

    @Before
    @Throws(SQLException::class)
    fun rollback() {
        val tables = dataSource.connection.metaData.getTables(
                dataSource.connection.catalog, dataSource.connection.schema, "%", null)

        em.flush()
        em.entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()

        while (tables.next()) {
            if (tables.getObject(3) == "CONFIGURATION" || tables.getObject(3) == "USER_STATS") {
                continue
            }

            em.entityManager.createNativeQuery("TRUNCATE TABLE \"" + tables.getObject(3) + "\"").executeUpdate()
        }

        em.entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }

    protected fun createRedG(): RedG {
        val redG = RedG()

        redG.dummyFactory = object : DefaultDummyFactory() {
            override fun <T : RedGEntity> getDummy(abstractRedG: AbstractRedG, dummyClass: Class<T>): T {
                @Suppress("NAME_SHADOWING")
                val redG = abstractRedG as RedG

                if (dummyClass == GUser::class.java) {
                    val rnd = 100 + (Math.random() * (99999 - 100)).toLong()

                    @Suppress("UNCHECKED_CAST")
                    return redG.addUser()
                            .id(rnd)
                            .username(rnd.toString() + "") as T
                } else {
                    return super.getDummy(redG, dummyClass)
                }
            }
        }

        return redG
    }
}
