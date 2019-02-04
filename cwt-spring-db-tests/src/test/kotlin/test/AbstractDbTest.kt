package test

import com.btc.redg.generated.GUser
import com.btc.redg.generated.RedG
import com.btc.redg.runtime.AbstractRedG
import com.btc.redg.runtime.RedGEntity
import com.btc.redg.runtime.defaultvalues.DefaultValueStrategyBuilder
import com.btc.redg.runtime.dummy.DefaultDummyFactory
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import org.hibernate.Session
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:postgresql://127.0.0.1:5433/postgres",
    "spring.flyway.locations=classpath:db/migration/common,classpath:db/migration/test"
])
abstract class AbstractDbTest {

    @Autowired
    protected lateinit var em: TestEntityManager

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

        redG.defaultValueStrategy = with(DefaultValueStrategyBuilder()) {
            whenTableNameMatches("tournament").andColumnNameMatches("status").thenUse(TournamentStatus.GROUP.name)
            build()
        }

        return redG
    }

    protected fun insertRedGIntoDatabase(redG: RedG) {
        em.entityManager.unwrap(Session::class.java).doWork { connection ->
            redG.insertDataIntoDatabase(connection)
        }
    }
}
