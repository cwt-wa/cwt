package test;

import com.btc.redg.generated.GUser;
import com.btc.redg.generated.RedG;
import com.btc.redg.runtime.AbstractRedG;
import com.btc.redg.runtime.RedGEntity;
import com.btc.redg.runtime.dummy.DefaultDummyFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public abstract class AbstractDbTest {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected TestEntityManager em;

    @Before
    public void rollback() throws SQLException {
        final ResultSet tables = dataSource.getConnection().getMetaData().getTables(
                dataSource.getConnection().getCatalog(), dataSource.getConnection().getSchema(), "%", null);

        em.flush();
        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        while (tables.next()) {
            if (Objects.equals(tables.getObject(3), "CONFIGURATION")) {
                continue;
            }

            em.getEntityManager().createNativeQuery("TRUNCATE TABLE \"" + tables.getObject(3) + "\"").executeUpdate();
        }

        em.getEntityManager().createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    protected RedG createRedG() {
        final RedG redG = new RedG();

        redG.setDummyFactory(new DefaultDummyFactory() {
            @Override
            public <T extends RedGEntity> T getDummy(AbstractRedG abstractRedG, Class<T> dummyClass) {
                RedG redG = (RedG) abstractRedG;

                if (dummyClass == GUser.class) {
                    final long rnd = 100 + (long) (Math.random() * (99999 - 100));
                    //noinspection unchecked
                    return (T) redG.addUser()
                            .id(rnd)
                            .username(rnd + "");
                } else {
                    return super.getDummy(redG, dummyClass);
                }
            }
        });

        return redG;
    }
}
