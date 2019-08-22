package com.cwtsite.cwt.domain.user.repository.entity

import org.assertj.core.api.Assertions
import org.junit.Test
import java.lang.reflect.Modifier

class AuthorityRoleTest {

    @Test
    fun `AuthorityName and AuthorityRole match`() {
        Assertions
                .assertThat(AuthorityName.values().map { it.name })
                .containsExactlyInAnyOrder(
                        *AuthorityRole::class.java.declaredFields
                                .filter { Modifier.isStatic(it.modifiers) }
                                .map { it.get(String::javaClass).toString() }
                                .toTypedArray())
    }
}
