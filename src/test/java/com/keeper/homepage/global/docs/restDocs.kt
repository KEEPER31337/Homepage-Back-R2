package com.keeper.homepage.global.docs

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.member.entity.job.MemberJob
import com.keeper.homepage.global.config.security.data.JwtType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class keeperRestDocsTest : IntegrationTest() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `레스트 독스` {
        @Test
        fun `test`() {
            val member = memberTestHelper.generate()
            member.assignJob(MemberJob.MemberJobType.ROLE_서기)
            val memberCookies = memberTestHelper.getTokenCookies(member)

            restDocs(mockMvc, HttpMethod.GET, "/merits/types") {
                expect(HttpStatus.OK, MediaType.APPLICATION_JSON_UTF8)
                cookie(*memberCookies)
                cookieVariable(
                        JwtType.ACCESS_TOKEN.tokenName means "Access Token",
                        JwtType.REFRESH_TOKEN.tokenName means "Refresh Token",
                )
                responseBodyWithPaging(
                        "content[].id" means "상벌점 타입의 ID",
                        "content[].score" means "상벌점 점수",
                        "content[].detail" means "상벌점 사유",
                        "content[].isMerit" means "상벌점 타입",
                )
            }

        }

    }

}