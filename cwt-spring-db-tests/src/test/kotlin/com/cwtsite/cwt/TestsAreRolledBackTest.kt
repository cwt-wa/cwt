package com.cwtsite.cwt

import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import test.AbstractDbTest

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
open class TestsAreRolledBackTest : AbstractDbTest() {

    companion object {
        private var test1HasBeenExecuted: Boolean = false
    }

    @Test
    fun test1() {
        test1HasBeenExecuted = true

        em.entityManager
                .createNativeQuery("INSERT INTO playoff_game (id, round, spot) VALUES (${Long.MAX_VALUE}, 1, 1);")
                .executeUpdate()
    }

    @Test
    fun test2() {
        Assert.assertTrue(test1HasBeenExecuted)

        Assert.assertTrue(em.entityManager
                .createNativeQuery("select * from playoff_game where id=${Long.MAX_VALUE}")
                .resultList
                .isEmpty())
    }
}
