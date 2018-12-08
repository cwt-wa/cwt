package com.cwtsite.cwt.domain.user.service;

import com.btc.redg.generated.GUserStats;
import com.btc.redg.generated.RedG;
import com.cwtsite.cwt.domain.user.repository.UserRepository;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import test.AbstractDbTest;

public class UserRepositoryTest extends AbstractDbTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAll() {
        final RedG redG = createRedG();

        redG.addUser().id(1L).username("1L")
                .userStatsIdUserStats(createUserStats(redG, 1L, 2, 17));
        redG.addUser().id(2L).username("2L")
                .userStatsIdUserStats(createUserStats(redG, 2L, 5, 2));
        redG.addUser().id(3L).userStatsIdUserStats(null);

        redG.insertDataIntoDatabase(dataSource);

        Assertions
                .assertThat(findAllActual("userStats.participations").getContent().stream().map(User::getId))
                .containsExactly(2L, 1L, 3L);

        Assertions
                .assertThat(findAllActual("userStats.trophyPoints").getContent().stream().map(User::getId))
                .containsExactly(1L, 2L, 3L);
    }

    private Page<User> findAllActual(String sortColumn) {
        return userRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, sortColumn)));
    }

    private GUserStats createUserStats(RedG redG, long userId, int participations, int trophyPoints) {
        return redG.addUserStats()
                .userId(userId)
                .participations(participations)
                .trophyPoints(trophyPoints);
    }
}
