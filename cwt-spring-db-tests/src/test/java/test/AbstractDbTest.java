package test;

import com.btc.redg.generated.GUser;
import com.btc.redg.generated.RedG;
import com.btc.redg.runtime.AbstractRedG;
import com.btc.redg.runtime.RedGEntity;
import com.btc.redg.runtime.dummy.DefaultDummyFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(properties = "spring.datasource.platform=test")
public abstract class AbstractDbTest {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected TestEntityManager em;

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
