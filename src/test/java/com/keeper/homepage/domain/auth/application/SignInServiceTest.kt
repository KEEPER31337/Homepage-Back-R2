package com.keeper.homepage.domain.auth.application

import com.keeper.homepage.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SignInServiceTest : IntegrationTest() {
    @Test
    fun `패스워드 임시 인증번호 요청을 하면 redis에 임시 인증번호가 들어있어야 한다`() {
        val member = memberTestHelper.generate()

        signInService.sendPasswordChangeAuthCode(member.profile.emailAddress, member.profile.loginId)

        assertThat(redisUtil.getData("PW_AUTH_" + member.id, String::class.java)).isNotEmpty
    }

    @Test
    fun `패스워드 임시 인증번호 요청을 2번하면 마지막 인증번호가 들어있어야 한다`() {
        val member = memberTestHelper.generate()

        signInService.sendPasswordChangeAuthCode(member.profile.emailAddress, member.profile.loginId)
        val firstAuthCode = redisUtil.getData("PW_AUTH_" + member.id, String::class.java).orElseThrow()
        signInService.sendPasswordChangeAuthCode(member.profile.emailAddress, member.profile.loginId)
        val secondAuthCode = redisUtil.getData("PW_AUTH_" + member.id, String::class.java).orElseThrow()

        assertThat(redisUtil.getData("PW_AUTH_" + member.id, String::class.java).get()).isNotEqualTo(firstAuthCode)
        assertThat(redisUtil.getData("PW_AUTH_" + member.id, String::class.java).get()).isEqualTo(secondAuthCode)
    }
}
